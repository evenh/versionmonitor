package net.evenh.versionmonitor;

import net.evenh.versionmonitor.models.projects.AbstractProject;
import net.evenh.versionmonitor.services.HostService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A registry of available hosts.
 *
 * @author Even Holthe
 * @since 2016-03-07
 */
@Component
public class HostRegistry {
  private static final Logger logger = LoggerFactory.getLogger(HostRegistry.class);
  private final Map<String, HostService> registry = new HashMap<>();

  private HostRegistry() {
  }

  public synchronized Optional<HostService> getHostService(final String hostIdentifier) {
    if (isRegistrered(hostIdentifier)) {
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

  private boolean isRegistrered(final String hostIdentifier) {
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
