package net.evenh.versionmonitor.application.subscriptions.types;

import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.Entity;
import javax.persistence.Table;
import net.evenh.versionmonitor.api.commands.AddSubscriptionCommand;
import net.evenh.versionmonitor.domain.subscriptions.Subscription;
import net.evenh.versionmonitor.infrastructure.View;

/**
 * A subscription of new release notifications.
 */
@Entity
@Table(name = "subscription_slack")
public class SlackSubscription extends Subscription {
  public SlackSubscription() {
    super();
  }

  /**
   * Create a Slack subscription from a subscription command.
   */
  public SlackSubscription(AddSubscriptionCommand command) {
    this.setIdentifier(command.getIdentifier());
    this.setName(command.getName());
    this.setChannel(command.getChannel());
  }

  @JsonView(View.Detail.class)
  private String channel;

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SlackSubscription that = (SlackSubscription) o;

    return this.getIdentifier().equalsIgnoreCase(that.getIdentifier())
      && this.getName().equalsIgnoreCase(that.getName());
  }

  @Override
  public String toString() {
    return "SlackSubscription{"
      + "name='" + getName() + '\''
      + " identifier='" + getIdentifier() + '\''
      + " channel='" + channel + '\''
      + '}';
  }

  @Override
  public int hashCode() {
    return getIdentifier().hashCode() + (channel != null ? channel.hashCode() : 0);
  }

  @Override
  public String getServiceName() {
    return "Slack";
  }
}
