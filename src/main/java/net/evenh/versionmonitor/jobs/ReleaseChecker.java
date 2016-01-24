package net.evenh.versionmonitor.jobs;

import net.evenh.versionmonitor.jobs.checkers.CheckerJob;
import net.evenh.versionmonitor.models.projects.AbstractProject;
import net.evenh.versionmonitor.models.projects.GitHubProject;
import net.evenh.versionmonitor.repositories.ProjectRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Schedules and delegates the checking for new releases.
 *
 * @author Even Holthe
 * @since 2016-01-17
 */
@Component
public class ReleaseChecker {
  private static final Logger logger = LoggerFactory.getLogger(ReleaseChecker.class);

  @Autowired
  private ProjectRepository repository;

  @Autowired
  @Qualifier("gitHubChecker")
  private CheckerJob github;

  /**
   * Performs the actual checking for new releases at a scheduled interval.
   */
  @Scheduled(fixedRate = 10_000)
  public void check() {
    List<AbstractProject> projects = repository.findAll();

    if (projects.isEmpty()) {
      logger.info("No projects found, skipping checks");
    }

    projects.forEach(project -> {
      if (project instanceof GitHubProject) {
        github.checkAndUpdate(project);
      }


    });
  }
}
