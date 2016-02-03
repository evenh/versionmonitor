package net.evenh.versionmonitor.models.projects;

import net.evenh.versionmonitor.models.Release;

import java.util.List;

/**
 * The software project interface specifies how to interact
 * with a specific host where a software project is hosted.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
public interface Project {
  /**
   * Gets the name of a software project.
   */
  String getName();

  /**
   * A description of the software project.
   *
   * <p><i>Note that all hosts may not have this information.</i>
   */
  String getDescription();

  /**
   * A unique identifier to identify this project with the host.
   *
   * <p>Can be a URL or anything a String can hold.
   */
  String getIdentifier();

  /**
   * Returns a list of releases for this software project.
   */
  List<Release> getReleases();

  /**
   * Add a release to the list of releases.
   *
   * @param release A <code>Release</code> object.
   */
  void addRelease(Release release);

  /**
   * Returns the URL of this project on a given host.
   *
   * @return The URL to the project on a host.
   */
  String getUrl();
}
