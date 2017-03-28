package net.evenh.versionmonitor.application.hosts.github;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.evenh.versionmonitor.domain.hosts.HostRegistry;
import net.evenh.versionmonitor.domain.hosts.HostService;
import net.evenh.versionmonitor.domain.projects.Project;
import net.evenh.versionmonitor.domain.projects.ProjectService;
import net.evenh.versionmonitor.domain.releases.Release;
import net.evenh.versionmonitor.domain.releases.ReleaseRepository;
import net.evenh.versionmonitor.infrastructure.config.VersionmonitorConfiguration;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;
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

/**
 * GitHub service is responsible for communicating with GitHub, including monitoring rate limits.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
@Service("gitHubHostService")
public class GitHubHostService extends AbstractHealthIndicator
    implements HostService, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(GitHubHostService.class);
  private final Pattern repoId = Pattern.compile("^[a-z0-9-_]+/[a-z0-9-_]+$", CASE_INSENSITIVE);

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

  private GitHub gitHub;

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
    final String authToken = props.getGithub().getOauthToken();

    // Validate token
    if (authToken == null || authToken.isEmpty()) {
      throw new IllegalArgumentException("Missing GitHub OAuth2 token");
    }

    // Setup GitHub connection and validate token
    try {
      gitHub = GitHubBuilder
        .fromEnvironment()
        .withOAuthToken(authToken)
        .withConnector(new OkHttpConnector(new OkUrlFactory(httpClient)))
        .build();
    } catch (IOException e) {
      log.warn("Caught exception while establishing a connection to GitHub", e);
      throw e;
    }

    // Register GitHubHostService with the host registry
    registry.register(this);

    log.info("GitHub service up and running. Logged in as: {}", gitHub.getMyself().getLogin());
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
      log.warn("Illegal arguments were supplied to the GitHubHostService", e);
    } catch (FileNotFoundException e) {
      log.warn("Project not found: {}", identifier);
    } catch (Exception e) {
      log.warn("Encountered problems using the GitHub service", e);
    }

    return Optional.empty();
  }

  @Override
  public boolean validIdentifier(String identifier) {
    return repoId.matcher(identifier).matches();
  }

  @Override
  public boolean isSatisfiedBy(Project project) {
    return (project instanceof GitHubProject);
  }

  @Override
  public List<Release> check(final Project project) throws Exception {
    Objects.requireNonNull("Supplied GitHub project cannot be null");

    if (!isSatisfiedBy(project)) {
      throw new IllegalArgumentException("Project is not a GitHub project: " + project);
    }

    final String prefix = this.getClass().getSimpleName() + "[" + project.getIdentifier() + "]: ";

    List<Release> newReleases = new ArrayList<>();

    if (hasReachedRateLimit()) {
      log.info(prefix + "Reached GitHub rate limit. Returning empty list of new releases.");
      return newReleases;
    }

    List<String> existingReleases = project.getReleases().stream()
        .map(Release::getVersion)
        .collect(Collectors.toList());

    try {
      Optional<GHRepository> repo = getRepository(project.getIdentifier());

      if (!repo.isPresent()) {
        log.warn(prefix + "Could not read fetch repo from database. Returning!");
        return newReleases;
      } else {
        repo.ifPresent(ghRepo -> {
          try {
            ghRepo.listTags().forEach(tag -> {
              if (!existingReleases.contains(tag.getName())) {
                final Release newRelease = mapToRelease(tag, project.getIdentifier());
                releases.saveAndFlush(newRelease);
                newReleases.add(newRelease);
              }
            });

            newReleases.forEach(project::addRelease);
            projectService.persist(project);
          } catch (IOException e) {
            log.warn(prefix + "Got exception while fetching tags", e);
          }
        });
      }

    } catch (FileNotFoundException e) {
      log.warn(prefix + "Project does not exist. Removed or bad access rights?");
    }

    log.debug(prefix + "Found {} new releases", newReleases.size());
    return newReleases;
  }

  @Override
  public String getHostIdentifier() {
    return "github";
  }


  /**
   * Convenience method to convert a {@link GHTag} to a {@link Release}.
   */
  private Release mapToRelease(GHTag tag, String identifier) {
    Date creationDate;

    try {
      creationDate = tag.getCommit().getAuthoredDate();
    } catch (IOException e) {
      creationDate = new Date(0);
    }

    return Release.builder()
      .withVersion(tag.getName())
      .withUrl("https://github.com/" + identifier + "/releases/tag/" + tag.getName())
      .withCreatedAt(creationDate)
      .build();
  }

  /**
   * Utility method to check if the rate limit is reached and log a bunch in the process.
   */
  private boolean hasReachedRateLimit() {
    // Check rate limit
    Optional<GHRateLimit> rateLimit = getRateLimit();

    if (rateLimit.isPresent()) {
      GHRateLimit rl = rateLimit.get();

      log.debug("{} of {} available calls performed", rl.remaining, rl.limit);
      log.debug("Rate limit will be reset on {}", rl.getResetDate());

      int callsLeft = rl.limit - (rl.remaining - props.getGithub().getRatelimitBuffer());

      if (callsLeft <= 0) {
        log.info("No GitHub calls remaining. No new release checks will be "
            + "attempted before {} has occurred", rl.remaining, rl.getResetDate());

        return true;
      }
    } else {
      log.warn("Could not get rate limit information! Assuming rate limit has been reached.");
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
      releases = repository.listTags().asList().stream()
        .map(tag -> mapToRelease(tag, identifier))
        .collect(Collectors.toList());
    } catch (IOException e) {
      log.warn("Encountered IOException while populating GitHub releases", e);
    }

    return releases;
  }

  /**
   * Gives a health indication based on the remaining API calls available.
   */
  @Override
  protected void doHealthCheck(Health.Builder builder) throws Exception {
    getRateLimit().ifPresent(rateLimit -> {
      builder.withDetail("buffer", props.getGithub().getRatelimitBuffer());
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


  /**
   * Constructs a <code>GHRepository</code> with populated data for a given identifier.
   *
   * @param ownerRepo A username and project in this form: <code>apple/swift</code> for describing
   *                  the repository located at https://github.com/apple/swift.
   * @return A populated <code>GHRepository</code> object with data about the requested repository.
   * @throws IllegalArgumentException Thrown if a malformed project identifier is provided as the
   *                                  input argument.
   * @throws FileNotFoundException    Thrown if the repository does not exist.
   */
  private Optional<GHRepository> getRepository(final String ownerRepo)
      throws IllegalArgumentException, FileNotFoundException {
    log.debug("Processing repository with identifier: {}", ownerRepo);

    if (ownerRepo == null || ownerRepo.isEmpty()) {
      throw new IllegalArgumentException("GitHub repository identifier is missing");
    } else if (!validIdentifier(ownerRepo)) {
      throw new IllegalArgumentException("Illegal GitHub repository identifier: " + ownerRepo);
    }

    try {
      return Optional.ofNullable(gitHub.getRepository(ownerRepo));
    } catch (FileNotFoundException e) {
      log.info("GitHub repository does not exist: {}", ownerRepo);
      throw e;
    } catch (IOException e) {
      log.warn("Got exception while fetching repository", e);
    }

    return Optional.empty();
  }

  /**
   * Gets information about the remaining rate limit.
   *
   * @return A optional <code>GHRateLimit</code> object.
   */
  private Optional<GHRateLimit> getRateLimit() {
    try {
      return Optional.ofNullable(gitHub.getRateLimit());
    } catch (IOException e) {
      log.warn("Got exception while requesting rate limit", e);
    }

    return Optional.empty();
  }
}
