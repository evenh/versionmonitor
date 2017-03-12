package net.evenh.versionmonitor.application.subscriptions;

import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import net.evenh.versionmonitor.api.commands.AddSubscriptionCommand;
import net.evenh.versionmonitor.domain.subscriptions.AbstractSubscription;
import net.evenh.versionmonitor.infrastructure.View;

/**
 * A subscription of new release notifications.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
@Entity
@Table(name = "subscriptions_slack")
public class SlackSubscription extends AbstractSubscription {
  public SlackSubscription() {
    super();
  }

  public SlackSubscription(AddSubscriptionCommand command) {
    this.setIdentifier(command.getIdentifier());
    this.setName(command.getName());
    this.setChannel(command.getChannel());
  }

  @Column(nullable = true)
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
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SlackSubscription that = (SlackSubscription) o;

    return this.getIdentifier().equalsIgnoreCase(that.getIdentifier()) && this.getName().equalsIgnoreCase(that.getName());
  }

  @Override
  public String toString() {
    return "SlackSubscription{" +
      "name='" + getName() + '\'' +
      " identifier='" + getIdentifier() + '\'' +
      " channel='" + channel + '\'' +
      '}';
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
