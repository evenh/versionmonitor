package net.evenh.versionmonitor.services;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.OkHttpConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

/**
 * GitHub service is responsible for communicating with GitHub, including monitoring rate limits.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
@Service("GitHubService")
public class GitHubService {
  private static GitHubService instance;
  private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

  @Value("${github.oauthToken}")
  private String authToken;
  private GitHub service;

  @Value("${github.cache.size}")
  private Integer cacheSize;

  private final Pattern matcher = Pattern.compile("^[a-z0-9-_]+/[a-z0-9-_]+$",
          Pattern.CASE_INSENSITIVE);

  /**
   * Gets an instance of GitHubService or creates the initial instance if not already available.
   */
  public static GitHubService getInstance() {
    if (instance == null) {
      logger.info("No instance of GitHubService exists. Instantiating one.");
      instance = new GitHubService();
    }

    return instance;
  }

  /**
   * Private constructor for singleton creation.
   *
   * @see GitHubService#connect()
   */
  private GitHubService() {
  }

  /**
   * Performs the initial connection to GitHub.
   *
   * @throws IllegalArgumentException Thrown if OAuth2 Token is not configured or improperly
   *                                  configured.
   * @throws IOException              Thrown if there is problems communicating with GitHub
   *                                  unrelated to the OAuth2 token.
   */
  @PostConstruct
  private void connect() throws IllegalArgumentException, IOException {
    if (authToken == null || authToken.isEmpty()) {
      throw new IllegalArgumentException("No GitHub OAuth2 token exists");
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
  }


  /**
   * Constructs a <code>GHRepository</code> with populated data for a given identifier.
   *
   * @param ownerRepo A username and project in this form: <code>apple/swift</code> for describing
   *                  the repository located at https://github.com/apple/swift.
   * @return A populated <code>GHRepository</code> object with data about the requested repository.
   *         Contains metadata and releases amongst other data.
   * @throws IllegalArgumentException Thrown if a malformed project identifier is provided as the
   *                                  input argument.
   */
  public Optional<GHRepository> getRepository(String ownerRepo) throws IllegalArgumentException {
    logger.debug("Repository identifier: {}", ownerRepo);

    if (ownerRepo == null || ownerRepo.isEmpty()) {
      throw new IllegalArgumentException("GitHub repository identifier is missing");
    } else if (!matcher.matcher(ownerRepo).matches()) {
      throw new IllegalArgumentException("Illegal GitHub repository identifier: " + ownerRepo);
    }

    try {
      return Optional.ofNullable(service.getRepository(ownerRepo));
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
}
