package net.evenh.versionmonitor.application.hosts.impl;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import net.evenh.versionmonitor.application.hosts.HostRegistry;
import net.evenh.versionmonitor.application.hosts.HostService;
import net.evenh.versionmonitor.application.projects.AbstractProject;
import net.evenh.versionmonitor.application.projects.ProjectService;
import net.evenh.versionmonitor.application.releases.ReleaseRepository;
import net.evenh.versionmonitor.domain.projects.GitHubProject;
import net.evenh.versionmonitor.domain.releases.Release;
import net.evenh.versionmonitor.infrastructure.config.VersionmonitorConfiguration;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.OkHttpConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.evenh.versionmonitor.domain.releases.Release.ReleaseBuilder;

/**
 * GitHub service is responsible for communicating with GitHub, including monitoring rate limits.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
@Service("gitHubHostService")
public class GitHubHostService extends AbstractHealthIndicator implements HostService, InitializingBean {
  private static final Logger logger = LoggerFactory.getLogger(GitHubHostService.class);

  @Autowired
  private HostRegistry registry;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private ReleaseRepository releases;

  @Autowired
  private VersionmonitorConfiguration props;

  @Autowired
  private OkHttpClient httpClient;

  private String authToken;

  private Integer rateLimitBuffer;

  private Integer cacheSize;

  private GitHub gitHub;

  private GitHubHostService() {
  }

  /**
   * Performs the initial connection to GitHub.
   *
   * @throws IllegalArgumentException Thrown if OAuth2 Token is not configured or improperly
   *                                  configured.
   * @throws IOException              Thrown if there is problems communicating with GitHub
   *                                  unrelated to the OAuth2 token.
   */
  @Override
  public void afterPropertiesSet() throws IllegalArgumentException, IOException {
    this.authToken = props.getGithub().getOauthToken();
    this.rateLimitBuffer = props.getGithub().getRatelimitBuffer();

    if (authToken == null || authToken.isEmpty()) {
      throw new IllegalArgumentException("Missing GitHub OAuth2 token");
    }

    try {
      gitHub = GitHubBuilder
        .fromEnvironment()
        .withOAuthToken(authToken)
        .withConnector(new OkHttpConnector(
          new OkUrlFactory(httpClient)
        ))
        .build();
    } catch (IOException e) {
      logger.warn("Caught exception while connecting to GitHub", e);
      throw e;
    }

    // Register GitHubHostService with the host registry
    registry.register(this);

    logger.info("GitHub service up and running");
  }


  /**
   * Constructs a <code>GHRepository</code> with populated data for a given identifier.
   *
   * @param ownerRepo A username and project in this form: <code>apple/swift</code> for describing
   *                  the repository located at https://github.com/apple/swift.
   * @return A populated <code>GHRepository</code> object with data about the requested repository.
   * Contains metadata and releases amongst other data.
   * @throws IllegalArgumentException Thrown if a malformed project identifier is provided as the
   *                                  input argument.
   * @throws FileNotFoundException    Thrown if the repository does not exist.
   */
  public Optional<GHRepository> getRepository(final String ownerRepo) throws IllegalArgumentException,
    FileNotFoundException {
    logger.debug("Processing repository with identifier: {}", ownerRepo);

    if (ownerRepo == null || ownerRepo.isEmpty()) {
      throw new IllegalArgumentException("GitHub repository identifier is missing");
    } else if (!validIdentifier(ownerRepo)) {
      throw new IllegalArgumentException("Illegal GitHub repository identifier: " + ownerRepo);
    }

    try {
      return Optional.ofNullable(gitHub.getRepository(ownerRepo));
    } catch (FileNotFoundException e) {
      logger.info("GitHub repository does not exist: {}", ownerRepo);
      throw e;
    } catch (IOException e) {
      logger.warn("Got exception while fetching repository", e);
    }

    return Optional.empty();
  }

  /**
   * Gets information about the remaining rate limit.
   *
   * @return A optional <code>GHRateLimit</code> object.
   */
  public Optional<GHRateLimit> getRateLimit() {
    try {
      return Optional.ofNullable(gitHub.getRateLimit());
    } catch (IOException e) {
      logger.warn("Got exception while requesting rate limit", e);
    }

    return Optional.empty();
  }

  @Override
  public Optional<GitHubProject> getProject(String identifier) {
    try {
      Optional<GHRepository> repoMaybe = getRepository(identifier);

      if (repoMaybe.isPresent()) {
        final GHRepository repo = repoMaybe.get();
        final GitHubProject project = new GitHubProject();

        project.setIdentifier(identifier);
        project.setName(repo.getName());
        project.setDescription(repo.getDescription());
        project.setReleases(populateGitHubReleases(repo, identifier));

        return Optional.of(project);
      }

      return Optional.empty();
    } catch (IllegalArgumentException e) {
      logger.warn("Illegal arguments were supplied to the GitHubHostService", e);
    } catch (FileNotFoundException e) {
      logger.warn("Project not found: {}", identifier);
    } catch (Exception e) {
      logger.warn("Encountered problems using the GitHub service", e);
    }

    return Optional.empty();
  }

  @Override
  public boolean validIdentifier(String identifier) {
    final Pattern matcher = Pattern.compile("^[a-z0-9-_]+/[a-z0-9-_]+$",
      Pattern.CASE_INSENSITIVE);

    return matcher.matcher(identifier).matches();
  }

  @Override
  public boolean satisfiedBy(AbstractProject project) {
    return (project instanceof GitHubProject);
  }

  @Override
  public List<Release> check(final AbstractProject project) throws Exception {
    Objects.requireNonNull("Supplied GitHub project cannot be null");

    if (!satisfiedBy(project)) {
      throw new IllegalArgumentException("Project is not a GitHub project: " + project);
    }

    final String prefix = this.getClass().getSimpleName() + "[" + project.getIdentifier() + "]: ";

    List<Release> newReleases = new ArrayList<>();

    if (hasReachedRateLimit()) {
      logger.info(prefix + "Reached GitHub rate limit. Returning empty list of new releases.");
      return newReleases;
    }

    List<String> existingReleases = project.getReleases().stream()
      .map(Release::getVersion)
      .collect(Collectors.toList());

    try {
      Optional<GHRepository> repo = getRepository(project.getIdentifier());

      if (!repo.isPresent()) {
        logger.warn(prefix + "Could not read fetch repo from database. Returning!");
        return newReleases;
      } else {
        repo.ifPresent(ghRepo -> {
          try {
            ghRepo.listTags().forEach(tag -> {
              if (!existingReleases.contains(tag.getName())) {
                Release newRelease = ReleaseBuilder.builder()
                  .fromGitHub(tag, project.getIdentifier())
                  .build();

                releases.saveAndFlush(newRelease);

                newReleases.add(newRelease);
              }
            });

            newReleases.forEach(project::addRelease);
            projectService.persist(project);
          } catch (IOException e) {
            logger.warn(prefix + "Got exception while fetching tags", e);
          }
        });
      }

    } catch (FileNotFoundException e) {
      logger.warn(prefix + "Project does not exist. Removed or bad access rights?");
    }

    logger.debug(prefix + "Found {} new releases", newReleases.size());
    return newReleases;
  }

  @Override
  public String getHostIdentifier() {
    return "github";
  }

  /**
   * Utility method to check if the rate limit is reached and log a bunch in the process.
   */
  private boolean hasReachedRateLimit() {
    // Check rate limit
    Optional<GHRateLimit> rl = getRateLimit();

    if (rl.isPresent()) {
      GHRateLimit rateLimit = rl.get();

      logger.debug("{}/{} calls performed", rateLimit.remaining, rateLimit.limit);
      logger.debug("Rate limit will be reset on {}", rateLimit.getResetDate());

      int callsLeft = rateLimit.limit - (rateLimit.remaining - rateLimitBuffer);

      if (callsLeft <= 0) {
        logger.info("No GitHub calls remaining. No new release checks will be "
          + "attempted before {} has occurred", rateLimit.remaining, rateLimit.getResetDate());

        return true;
      }
    } else {
      logger.warn("Could not get rate limit information! Cancelling future calls");
      return true;
    }

    return false;
  }

  /**
   * Processes a repository and creates domain releases.
   *
   * @param repository A GitHub repository
   * @param identifier The identifier with GitHub.
   * @return A list of releases, which may be empty
   */
  private List<Release> populateGitHubReleases(GHRepository repository, String identifier) {
    List<Release> releases = new ArrayList<>();

    try {
      Release.ReleaseBuilder mapper = ReleaseBuilder.builder();

      releases = repository.listTags().asList()
        .stream()
        .map(tag -> mapper.fromGitHub(tag, identifier).build())
        .collect(Collectors.toList());
    } catch (IOException e) {
      logger.warn("Encountered IOException while populating GitHub releases", e);
    }

    return releases;
  }

  /**
   * Gives a health indication based on the remaining API calls available.
   */
  @Override
  protected void doHealthCheck(Health.Builder builder) throws Exception {
    getRateLimit().ifPresent(rateLimit -> {
      builder.withDetail("buffer", rateLimitBuffer);
      builder.withDetail("limit", rateLimit.limit);
      builder.withDetail("remaining", rateLimit.remaining);
      builder.withDetail("resetDate", rateLimit.getResetDate());
    });

    if (hasReachedRateLimit()) {
      builder.down();
    } else {
      builder.up();
    }
  }
}
