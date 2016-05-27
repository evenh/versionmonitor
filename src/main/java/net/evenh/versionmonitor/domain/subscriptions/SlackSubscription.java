package net.evenh.versionmonitor.domain.subscriptions;

import net.evenh.versionmonitor.api.commands.AddSubscriptionCommand;
import net.evenh.versionmonitor.application.subscriptions.AbstractSubscription;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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

    return (this.getIdentifier().equals(that.getIdentifier()) && this.getChannel().equalsIgnoreCase(that.getChannel()));
  }

  @Override
  public String toString() {
    return "SlackSubscription{" +
      "name='" + getName() + '\'' +
      "identifier='" + getIdentifier() + '\'' +
      "channel='" + channel + '\'' +
      '}';
  }

  @Override
  public String getServiceName() {
    return "Slack";
  }
}
