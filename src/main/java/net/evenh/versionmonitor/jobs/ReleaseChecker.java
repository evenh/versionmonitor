package net.evenh.versionmonitor.jobs;

import net.evenh.versionmonitor.jobs.checkers.CheckerJob;
import net.evenh.versionmonitor.models.Release;
import net.evenh.versionmonitor.models.Subscription;
import net.evenh.versionmonitor.models.notifications.SlackNotification;
import net.evenh.versionmonitor.models.projects.AbstractProject;
import net.evenh.versionmonitor.models.projects.GitHubProject;
import net.evenh.versionmonitor.repositories.ProjectRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

  @Autowired
  private SlackNotification slack;

  /**
   * Performs the actual checking for new releases at a scheduled interval.
   */
  @Scheduled(cron = "${versionmonitor.scheduling.cron}")
  public void check() {
    List<AbstractProject> projects = repository.findAll();

    if (projects.isEmpty()) {
      logger.info("No projects found, skipping checks");
      return;
    }

    List<Release> newReleases = new ArrayList<>();

    projects.forEach(project -> {
      if (project instanceof GitHubProject) {
        List<Release> releases = github.checkAndUpdate(project);

        if (!releases.isEmpty()) {
          releases.forEach(newReleases::add);
        }
      }
    });

    logger.info("Found a total of {} new releases", newReleases.size());

    // TODO: Not have static hook
    Subscription s = new Subscription();
    s.setId(1L);
    s.setIdentifier("https://hooks.slack.com/services/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");
    s.setName("Koderiet-org");

    newReleases.forEach(release -> slack.sendNotification(release, s));
  }
}
