package net.evenh.versionmonitor.domain.notifications;

import net.evenh.versionmonitor.application.notifications.Notification;
import net.evenh.versionmonitor.application.projects.AbstractProject;
import net.evenh.versionmonitor.application.projects.ProjectRepository;
import net.evenh.versionmonitor.application.subscriptions.AbstractSubscription;
import net.evenh.versionmonitor.domain.Release;
import net.evenh.versionmonitor.domain.subscriptions.SlackSubscription;
import net.evenh.versionmonitor.infrastructure.config.VersionmonitorConfiguration;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handles notification of new releases via Slack.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
@Component
public class SlackNotification implements Notification, InitializingBean {
  private static final Logger logger = LoggerFactory.getLogger(SlackNotification.class);

  @Autowired
  private VersionmonitorConfiguration props;

  @Autowired
  private ProjectRepository projects;

  private String botname;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.botname = props.getSlack().getBotname();
  }

  @Override
  public boolean sendNotification(Release release, AbstractSubscription subscription) {
    if(!(subscription instanceof SlackSubscription)) {
      throw new IllegalStateException("Expected an instance of SlackSubscription");
    }

    final SlackSubscription slackSubscription = (SlackSubscription) subscription;

    try {
      SlackApi api = new SlackApi(slackSubscription.getIdentifier());

      constructMessage(release)
        .ifPresent(message -> {
          message.setChannel(slackSubscription.getChannel());
          api.call(message);

          logger.debug("Successfully notified: {} of a new release. {}", subscription, release);
        });
    } catch (RuntimeException e) {
      logger.warn("Could not create Slack notification for release: {}", release, e);

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

    logger.warn("Could not construct SlackMessage for release: {}", release);

    return Optional.empty();
  }
}
