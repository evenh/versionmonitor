package net.evenh.versionmonitor.application.jobs;

import net.evenh.versionmonitor.application.hosts.HostRegistry;
import net.evenh.versionmonitor.domain.Release;
import net.evenh.versionmonitor.domain.subscriptions.SlackSubscription;
import net.evenh.versionmonitor.domain.notifications.SlackNotification;
import net.evenh.versionmonitor.application.projects.AbstractProject;
import net.evenh.versionmonitor.application.projects.ProjectRepository;
import net.evenh.versionmonitor.infrastructure.config.VersionmonitorConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  private HostRegistry registry;

  @Autowired
  private SlackNotification slack;

  @Autowired
  private VersionmonitorConfiguration config;

  /**
   * Performs the actual checking for new releases at a scheduled interval.
   */
  @Scheduled(cron = "${versionmonitor.jobchecker.cron}")
  public void check() {
    List<AbstractProject> projects = repository.findAll();

    if (projects.isEmpty()) {
      logger.info("No projects found, skipping checks");
      return;
    }

    final List<Release> releases = new ArrayList<>();

    projects.forEach(project -> registry.forProject(project).ifPresent(host -> {
      try {
        host.check(project).stream().forEach(releases::add);
      } catch (Exception e) {
        logger.warn("Got exception while checking for updates for {}", project, e);
      }
    }));

    logger.info("Found a total of {} new releases", releases.size());

    // TODO: Not have static hook
    SlackSubscription s = new SlackSubscription();
    s.setId(1L);
    s.setIdentifier(config.getSlack().getWebhookUrl());
    s.setChannel("#mychannel");
    s.setName("PxSlack");

    releases.forEach(release -> slack.sendNotification(release, s));
  }
}
