package net.evenh.versionmonitor.services.hosts;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import net.evenh.versionmonitor.HostRegistry;
import net.evenh.versionmonitor.models.Release;
import net.evenh.versionmonitor.models.projects.GitHubProject;
import net.evenh.versionmonitor.services.HostService;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.OkHttpConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * GitHub service is responsible for communicating with GitHub, including monitoring rate limits.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
@Service("gitHubService")
public class GitHubService implements HostService, InitializingBean {
  private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

  @Value("${github.oauthToken}")
  private String authToken;

  private GitHub service;

  @Value("${github.cache.size}")
  private Integer cacheSize;

  private final Pattern matcher = Pattern.compile("^[a-z0-9-_]+/[a-z0-9-_]+$",
          Pattern.CASE_INSENSITIVE);

  private GitHubService() {
    HostRegistry registry = HostRegistry.getInstance();
    registry.register(this);
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
    if (authToken == null || authToken.isEmpty()) {
      throw new IllegalArgumentException("Missing GitHub OAuth2 token");
    }

    try {
      // Set up caching
      File cacheDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
      Cache cache = new Cache(cacheDir, cacheSize * 1024 * 1024);

      service = GitHubBuilder
              .fromEnvironment()
              .withOAuthToken(authToken)
              .withConnector(new OkHttpConnector(
                      new OkUrlFactory(new OkHttpClient().setCache(cache))
              ))
              .build();

    } catch (IOException e) {
      logger.warn("Caught exception while connecting to GitHub", e);
      throw e;
    }

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
  public Optional<GHRepository> getRepository(String ownerRepo) throws IllegalArgumentException,
          FileNotFoundException {
    logger.debug("Repository identifier: {}", ownerRepo);

    if (ownerRepo == null || ownerRepo.isEmpty()) {
      throw new IllegalArgumentException("GitHub repository identifier is missing");
    } else if (!matcher.matcher(ownerRepo).matches()) {
      throw new IllegalArgumentException("Illegal GitHub repository identifier: " + ownerRepo);
    }

    try {
      return Optional.ofNullable(service.getRepository(ownerRepo));
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
      GHRateLimit rateLimit = service.getRateLimit();
      return rateLimit == null ? Optional.empty() : Optional.of(rateLimit);
    } catch (IOException e) {
      logger.warn("Got exception while requesting rate limit", e);
    }

    return Optional.empty();
  }

  @Override
  public Optional<GitHubProject> getProject(String identifier) {
    try {
      final GitHubProject project = new GitHubProject();

      getRepository(identifier).ifPresent(repository -> {
        project.setIdentifier(identifier);
        project.setName(repository.getName());
        project.setDescription(repository.getDescription());
        project.setReleases(populateGitHubReleases(repository, identifier));
      });

      return Optional.of(project);
    } catch (IllegalArgumentException e) {
      logger.warn("Illegal arguments were supplied to the GitHubService", e);
    } catch (FileNotFoundException e) {
      logger.warn("Project not found: {}", identifier);
    } catch (Exception e) {
      logger.warn("Encountered problems using the GitHub service", e);
    }

    return Optional.empty();
  }

  @Override
  public String getHostIdentifier() {
    return "github";
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
      Release.ReleaseBuilder mapper = Release.ReleaseBuilder.builder();

      releases = repository.listTags().asList()
              .stream()
              .map(tag -> mapper.fromGitHub(tag, identifier).build())
              .collect(Collectors.toList());
    } catch (IOException e) {
      logger.warn("Encountered IOException while populating GitHub releases", e);
    }

    return releases;
  }
}
