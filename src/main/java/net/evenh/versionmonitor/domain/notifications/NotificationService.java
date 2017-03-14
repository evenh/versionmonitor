package net.evenh.versionmonitor.domain.notifications;

import net.evenh.versionmonitor.domain.releases.Release;

/**
 * A service that can publish information on new {@link Release}s through various providers.
 */
public interface NotificationService {
  /**
   * Notify users through the supported providers of a new release.
   */
  void notify(Release release);
}
