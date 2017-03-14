package net.evenh.versionmonitor.application.notifications.provider;

import java.util.Optional;
import net.evenh.versionmonitor.application.subscriptions.types.SlackSubscription;
import net.evenh.versionmonitor.domain.notifications.NotificationProvider;
import net.evenh.versionmonitor.domain.projects.Project;
import net.evenh.versionmonitor.domain.projects.ProjectService;
import net.evenh.versionmonitor.domain.releases.Release;
import net.evenh.versionmonitor.domain.subscriptions.Subscription;
import net.evenh.versionmonitor.infrastructure.config.VersionmonitorConfiguration;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles notification of new releases via Slack.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
@Component
public class SlackNotificationProvider implements NotificationProvider {
  private static final Logger logger = LoggerFactory.getLogger(SlackNotificationProvider.class);

  @Autowired
  private VersionmonitorConfiguration props;

  @Autowired
  private ProjectService service;

  @Override
  public boolean sendNotification(Release release, Subscription subscription) {
    if(!(subscription instanceof SlackSubscription)) {
      throw new IllegalStateException("Expected an instance of SlackSubscription");
    }

    final SlackSubscription slackSubscription = (SlackSubscription) subscription;

    try {
      SlackApi api = new SlackApi(slackSubscription.getIdentifier());

      constructMessage(release)
        .ifPresent(message -> {
          if(slackSubscription.getChannel() != null) {
            message.setChannel(slackSubscription.getChannel());
          }

          api.call(message);

          logger.debug("Successfully notified: {} of a new release. {}", subscription, release);
        });
    } catch (RuntimeException e) {
      logger.warn("Could not create Slack notification for release: {}", release, e);

      return false;
    }

    return true;
  }

  @Override
  public boolean isSatisfiedBy(Subscription providerSubscription) {
    return providerSubscription.getClass().isAssignableFrom(SlackSubscription.class);
  }

  /**
   * Creates a <code>SlackMessage</code> object containing the actual text that is received
   * in the Slack channel.
   *
   * @param release A release which shall be announced.
   * @return A properly constructed <code>SlackMessage</code> object.
   */
  private Optional<SlackMessage> constructMessage(Release release) {
    Optional<Project> projectMaybe = service.findByRelease(release);

    if (projectMaybe.isPresent()) {
      Project project = projectMaybe.get();

      String rawText = "Version <"
              + release.getUrl()
              + "|" + release.getVersion()
              + "> of <"
              + project.getProjectUrl()
              + "|"
              + project.getName()
              + "> is available";

      SlackMessage msg = new SlackMessage(props.getSlack().getBotname(), rawText);
      msg.setIcon(":exclamation:");

      return Optional.of(msg);
    }

    logger.warn("Could not construct SlackMessage for release: {}", release);

    return Optional.empty();
  }
}
