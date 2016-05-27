package net.evenh.versionmonitor.application.hosts.impl;

import net.evenh.versionmonitor.application.hosts.HostRegistry;
import net.evenh.versionmonitor.application.hosts.HostService;
import net.evenh.versionmonitor.application.projects.AbstractProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class DefaultHostRegistry implements HostRegistry {
  private static final Logger logger = LoggerFactory.getLogger(HostRegistry.class);
  private final Map<String, HostService> registry = new HashMap<>();

  private DefaultHostRegistry() {
  }

  /**
   * Gets a particular host service by it's identifier.
   *
   * @param hostIdentifier The unique host identifier.
   */
  public synchronized Optional<HostService> getHostService(final String hostIdentifier) {
    if (isRegistered(hostIdentifier)) {
      return Optional.of(registry.get(hostIdentifier));
    }

    return Optional.empty();
  }

  public synchronized void register(final HostService service) {
    registry.put(service.getHostIdentifier(), service);
  }

  public synchronized void unregister(final HostService service) {
    unregister(service.getHostIdentifier());
  }

  public synchronized void unregister(final String serviceIdentifier) {
    registry.remove(serviceIdentifier);
  }

  public synchronized Set<String> getHosts() {
    return registry.keySet();
  }

  private boolean isRegistered(final String hostIdentifier) {
    return registry.containsKey(hostIdentifier);
  }

  public Optional<HostService> forProject(AbstractProject project) {
    for (HostService host : registry.values()) {
      if (host.satisfiedBy(project)) {
        return Optional.of(host);
      }
    }

    return Optional.empty();
  }
}
