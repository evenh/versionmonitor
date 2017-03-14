package net.evenh.versionmonitor.application.notifications;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.Set;
import net.evenh.versionmonitor.domain.notifications.NotificationProvider;
import net.evenh.versionmonitor.domain.notifications.NotificationService;
import net.evenh.versionmonitor.domain.projects.Project;
import net.evenh.versionmonitor.domain.projects.ProjectRepository;
import net.evenh.versionmonitor.domain.releases.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultNotificationService implements NotificationService {
  private static final Logger log = LoggerFactory.getLogger(DefaultNotificationService.class);
  private final Set<NotificationProvider> providers;

  @Autowired
  private ProjectRepository projects;

  @Autowired
  public DefaultNotificationService(Set<NotificationProvider> providers) {
    this.providers = providers;
  }

  @Override
  public void notify(Release release) {
    if (providers.isEmpty()) {
      log.warn("No providers - cannot notify anyone about the new release");
      return;
    }

    requireNonNull(release, "Invalid release specified");

    Optional<Project> project  = projects.findByRelease(release);

    if (project.isPresent()) {
      project.get().getSubscriptions()
          .forEach(subscription -> providers.stream()
            .filter(provider -> provider.isSatisfiedBy(subscription))
            .forEach(provider -> provider.sendNotification(release, subscription)));
    } else {
      log.warn("Found a dangling release which is missing project, {}", release);
    }
  }
}
