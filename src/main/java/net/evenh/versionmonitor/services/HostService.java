package net.evenh.versionmonitor.services;

import net.evenh.versionmonitor.models.projects.AbstractProject;

import java.util.Optional;

/**
 * Host protocol.
 *
 * @author Even Holthe
 * @since 2016-03-07
 */
public interface HostService {
  Optional<? extends AbstractProject> getProject(String identifier);
  String getHostIdentifier();
}
