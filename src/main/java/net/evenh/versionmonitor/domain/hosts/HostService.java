package net.evenh.versionmonitor.domain.hosts;

import java.util.List;
import java.util.Optional;
import net.evenh.versionmonitor.domain.projects.Project;
import net.evenh.versionmonitor.domain.releases.Release;

/**
 * Host protocol.
 *
 * @author Even Holthe
 * @since 2016-03-07
 */
public interface HostService {
  /**
   * Checks whether a identifier is valid for a given host.
   *
   * @param identifier The project identifier.
   * @return True if valid, false otherwise.
   */
  boolean validIdentifier(final String identifier);

  /**
   * Checks whether a project is instance of an acceptable subtype.
   *
   * @param project A {@link Project} subclass.
   * @return True if the given host can process the given project.
   */
  boolean satisfiedBy(final Project project);

  /**
   * An identifier that is unique across the system.
   *
   * @return A string representation.
   */
  String getHostIdentifier();

  /**
   * Gets a single unique project from a given software host.
   *
   * @param identifier The unique identifier that identifies a project with this host.
   */
  Optional<? extends Project> getProject(final String identifier);

  /**
   * Checks for new releases and updates the database if new releases is found.
   *
   * @return The new releases found.
   */
  List<Release> check(final Project project) throws Exception;
}
