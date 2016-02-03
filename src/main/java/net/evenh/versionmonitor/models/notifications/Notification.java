package net.evenh.versionmonitor.models.notifications;

import net.evenh.versionmonitor.models.Release;
import net.evenh.versionmonitor.models.Subscription;

/**
 * A generic notification for notifying subscribers of new releases.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
public interface Notification {
  boolean sendNotification(Release release, Subscription subscription);
}
