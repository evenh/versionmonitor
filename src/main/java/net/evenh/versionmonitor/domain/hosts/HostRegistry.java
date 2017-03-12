package net.evenh.versionmonitor.domain.hosts;

import java.util.Optional;
import java.util.Set;
import net.evenh.versionmonitor.domain.projects.AbstractProject;

/**
 * A registry of available hosts.
 */
public interface HostRegistry {
  /**
   * Gets a particular host service by it's identifier.
   *
   * @param hostIdentifier The unique host identifier.
   */
  Optional<HostService> getHostService(final String hostIdentifier);

  /**
   * Register a particular host service with the registry.
   */
  void register(final HostService service);

  /**
   * Unregister a particular host service from the registry.
   */
  void unregister(final HostService service);

  /**
   * Unregister a particular host service from the registry.
   */
  void unregister(final String serviceIdentifier);

  /**
   * Gets all available hosts.
   */
  Set<String> getHosts();

  /**
   * Lookup a given host service implementation for a project.
   */
  Optional<HostService> forProject(AbstractProject project);
}
