package net.evenh.versionmonitor.domain.notifications;

import net.evenh.versionmonitor.domain.releases.Release;
import net.evenh.versionmonitor.domain.subscriptions.Subscription;

/**
 * A system that can send send messages.
 */
public interface NotificationProvider {
  /**
   * Publish a new {@link Release} through the provider, given a valid {@link Subscription}.
   */
  boolean sendNotification(Release release, Subscription subscription);

  /**
   * Determines whether this provider supports a given {@link Subscription}.
   */
  boolean isSatisfiedBy(Subscription providerSubscription);
}
