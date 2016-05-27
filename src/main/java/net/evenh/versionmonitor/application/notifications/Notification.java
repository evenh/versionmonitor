package net.evenh.versionmonitor.application.notifications;

import net.evenh.versionmonitor.application.subscriptions.AbstractSubscription;
import net.evenh.versionmonitor.domain.Release;

/**
 * A generic notification for notifying subscribers of new releases.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
public interface Notification {
  boolean sendNotification(Release release, AbstractSubscription subscription);
}
