package net.evenh.versionmonitor.services;

import net.evenh.versionmonitor.models.Release;
import net.evenh.versionmonitor.models.projects.AbstractProject;

import java.util.List;
import java.util.Optional;

/**
 * Host protocol.
 *
 * @author Even Holthe
 * @since 2016-03-07
 */
public interface HostService {
  /**
   * Gets a single unique project from a given software host.
   *
   * @param identifier The unique identifier that identifies a project with this host.
   */
  Optional<? extends AbstractProject> getProject(final String identifier);

  /**
   * Checks whether a project is of an acceptable subtype.
   *
   * @param project A {@link AbstractProject} subclass.
   * @return True if the given host can process the given project.
   */
  boolean satisfiedBy(final AbstractProject project);

  /**
   * Checks for new releases and updates the database if new releases is found.
   *
   * @return The new releases found.
   */
  List<Release> check(final AbstractProject project) throws Exception;

  /**
   * An identifier that is unique across the system.
   *
   * @return A string representation.
   */
  String getHostIdentifier();
}
