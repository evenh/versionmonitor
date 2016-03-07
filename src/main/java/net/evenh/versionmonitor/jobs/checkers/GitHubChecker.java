package net.evenh.versionmonitor.jobs.checkers;

import net.evenh.versionmonitor.models.Release;
import net.evenh.versionmonitor.models.projects.AbstractProject;
import net.evenh.versionmonitor.models.projects.GitHubProject;
import net.evenh.versionmonitor.models.projects.Project;
import net.evenh.versionmonitor.repositories.ProjectRepository;
import net.evenh.versionmonitor.repositories.ReleaseRepository;
import net.evenh.versionmonitor.services.hosts.GitHubService;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks GitHub for new releases for a given project.
 *
 * @author Even Holthe
 * @since 2016-01-17
 */
@Component("gitHubChecker")
@Transactional
public class GitHubChecker implements CheckerJob {
  private static final Logger logger = LoggerFactory.getLogger(GitHubChecker.class);

  @Autowired
  private ProjectRepository repository;

  @Autowired
  private ReleaseRepository releases;

  @Autowired
  @Qualifier("gitHubService")
  private GitHubService service;

  private GitHubProject project;
  private String logPrefix;

  @Value("${github.ratelimit.buffer}")
  private Integer rateLimitBuffer;

  private GitHubChecker() {
  }

  private boolean init(Project project) {
    if (project instanceof GitHubProject) {
      this.project = (GitHubProject) project;
      this.logPrefix = this.getClass().getSimpleName() + "[" + project.getIdentifier() + "]: ";

      return true;
    }

    return false;
  }

  private boolean hasReachedRateLimit() {
    // Check rate limit
    Optional<GHRateLimit> rl = service.getRateLimit();

    if (rl.isPresent()) {
      GHRateLimit rateLimit = rl.get();

      logger.debug(logPrefix + "{}/{} calls performed", rateLimit.remaining, rateLimit.limit);
      logger.debug(logPrefix + "Rate limit will be reset on {}", rateLimit.getResetDate());

      int callsLeft = rateLimit.limit - (rateLimit.remaining - rateLimitBuffer);

      if (callsLeft <= 0) {
        logger.info(logPrefix + "No GitHub calls remaining. No new release checks will be "
                + "attempted before {} has occured", rateLimit.remaining, rateLimit.getResetDate());

        return true;
      }
    } else {
      logger.warn(logPrefix + "Could not get rate limit information! Cancelling future calls");
      return true;
    }

    return false;
  }

  @Override
  public List<Release> check(Project project) throws IllegalArgumentException {
    if (!init(project)) {
      throw new IllegalArgumentException("Project is not a GitHub project: " + project);
    }

    logger.debug(logPrefix + "Checking for new releases");

    List<Release> newReleases = new ArrayList<>();

    if (hasReachedRateLimit()) {
      return newReleases;
    }

    List<String> existingReleases = project.getReleases()
            .stream()
            .map(Release::getVersion)
            .collect(Collectors.toList());

    try {
      Optional<GHRepository> repo = service.getRepository(project.getIdentifier());

      if (!repo.isPresent()) {
        logger.warn(logPrefix + "Could not read fetch repo from database. Returning!");
        return newReleases;
      } else {
        repo.ifPresent(ghRepo -> {
          try {
            ghRepo.listTags().forEach(tag -> {
              if (!existingReleases.contains(tag.getName())) {
                Release newRelease = Release.ReleaseBuilder.builder()
                        .fromGitHub(tag, project.getIdentifier())
                        .build();

                releases.saveAndFlush(newRelease);

                newReleases.add(newRelease);
              }
            });

            if (!newReleases.isEmpty()) {
              newReleases.forEach(project::addRelease);

              repository.save((AbstractProject) project);
            }
          } catch (IOException e) {
            logger.warn(logPrefix + "Got exception while fetching tags", e);
          }
        });
      }

    } catch (FileNotFoundException e) {
      logger.warn(logPrefix + "Project does not exist. Removed or bad access rights?");
    }

    logger.debug(logPrefix + "Found {} new releases", newReleases.size());
    return newReleases;
  }
}
