package net.evenh.versionmonitor;

import net.evenh.versionmonitor.services.HostService;

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
public class HostRegistry {
  private static HostRegistry instance;
  private final Map<String, HostService> registry = new HashMap<>();

  public static synchronized HostRegistry getInstance() {
    if (instance == null) {
      instance = new HostRegistry();
    }
    return instance;
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
}
