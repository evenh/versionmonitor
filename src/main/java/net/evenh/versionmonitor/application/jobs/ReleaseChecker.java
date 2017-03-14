package net.evenh.versionmonitor.application.jobs;

import java.util.ArrayList;
import java.util.List;
import net.evenh.versionmonitor.domain.hosts.HostRegistry;
import net.evenh.versionmonitor.domain.notifications.NotificationService;
import net.evenh.versionmonitor.domain.projects.Project;
import net.evenh.versionmonitor.domain.projects.ProjectService;
import net.evenh.versionmonitor.domain.releases.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
  private ProjectService service;

  @Autowired
  private HostRegistry registry;

  @Autowired
  private NotificationService notificationService;

  /**
   * Performs the actual checking for new releases at a scheduled interval.
   */
  @Scheduled(cron = "${versionmonitor.jobchecker.cron}")
  public void check() {
    List<Project> projects = service.findAll();

    if (projects.isEmpty()) {
      logger.info("No projects found, skipping checks");
      return;
    }

    final List<Release> releasesFound = new ArrayList<>();

    projects.forEach(project -> registry.forProject(project).ifPresent(host -> {
      try {
        host.check(project).forEach(releasesFound::add);
      } catch (Exception e) {
        logger.warn("Got exception while checking for updates for {}", project, e);
      }
    }));

    logger.info("Found a total of {} new releases", releasesFound.size());

    releasesFound.forEach(notificationService::notify);
  }
}
