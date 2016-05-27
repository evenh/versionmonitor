package net.evenh.versionmonitor.application.hosts;

import net.evenh.versionmonitor.application.projects.AbstractProject;

import java.util.Optional;
import java.util.Set;

/**
 * A registry of available hosts.
 *
 * @author Even Holthe
 */
public interface HostRegistry {
  /**
   * Gets a particular host service by it's identifier.
   *
   * @param hostIdentifier The unique host identifier.
   */
  Optional<HostService> getHostService(final String hostIdentifier);

  void register(final HostService service);

  void unregister(final HostService service);

  void unregister(final String serviceIdentifier);

  Set<String> getHosts();

  Optional<HostService> forProject(AbstractProject project);
}
