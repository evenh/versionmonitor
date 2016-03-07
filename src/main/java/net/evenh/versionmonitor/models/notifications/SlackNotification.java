package net.evenh.versionmonitor.models.notifications;

import net.evenh.versionmonitor.models.Release;
import net.evenh.versionmonitor.models.Subscription;
import net.evenh.versionmonitor.models.projects.AbstractProject;
import net.evenh.versionmonitor.repositories.ProjectRepository;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handles notification of new releases via Slack.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
@Component
public class SlackNotification implements Notification {
  private static final Logger logger = LoggerFactory.getLogger(SlackNotification.class);

  @Autowired
  private ProjectRepository projects;

  @Value("${versionmonitor.notifications.slack.botname}")
  private String botname;

  @Override
  public boolean sendNotification(Release release, Subscription subscription) {
    try {
      SlackApi api = new SlackApi(subscription.getIdentifier());

      Optional<SlackMessage> message = constructMessage(release);

      if (message.isPresent()) {
        api.call(message.get());
        logger.debug("Successfully notified: {} of a new release. {}", subscription, release);
      } else {
        logger.warn("Could not construct SlackMessage for release: {}", release);
      }

    } catch (RuntimeException e) {
      logger.warn("Could not Slack notification for release: " + release, e);

      return false;
    }

    return true;
  }

  /**
   * Creates a <code>SlackMessage</code> object containing the actual text that is received
   * in the Slack channel.
   *
   * @param release A release which shall be announced.
   * @return A properly constructed <code>SlackMessage</code> object.
   */
  private Optional<SlackMessage> constructMessage(Release release) {
    Optional<AbstractProject> projectMaybe = projects.findByRelease(release);

    if (projectMaybe.isPresent()) {
      AbstractProject project = projectMaybe.get();

      String rawText = "Version <"
              + release.getUrl()
              + "|" + release.getVersion()
              + "> of <"
              + project.getProjectUrl()
              + "|"
              + project.getName()
              + "> is available";

      SlackMessage msg = new SlackMessage(botname, rawText);
      msg.setIcon(":exclamation:");

      return Optional.of(msg);
    }

    return Optional.empty();
  }
}
