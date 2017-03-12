package net.evenh.versionmonitor.domain.hosts;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.evenh.versionmonitor.domain.projects.AbstractProject;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class DefaultHostRegistry extends AbstractHealthIndicator implements HostRegistry {
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

  @Override
  protected void doHealthCheck(Health.Builder builder) throws Exception {
    builder.withDetail("hosts_supported", getHosts());
    builder.up();
  }
}
