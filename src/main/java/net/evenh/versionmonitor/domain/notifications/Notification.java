package net.evenh.versionmonitor.domain.notifications;

import net.evenh.versionmonitor.domain.Release;
import net.evenh.versionmonitor.domain.Subscription;

/**
 * A generic notification for notifying subscribers of new releases.
 *
 * @author Even Holthe
 * @since 2016-02-03
 */
public interface Notification {
  boolean sendNotification(Release release, Subscription subscription);
}
