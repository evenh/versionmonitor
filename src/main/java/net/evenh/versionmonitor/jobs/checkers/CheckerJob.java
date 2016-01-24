package net.evenh.versionmonitor.jobs.checkers;

import net.evenh.versionmonitor.models.Project;
import net.evenh.versionmonitor.models.Release;

import java.util.List;

/**
 * An interface describing how release checkers shall work.
 *
 * @author Even Holthe
 * @since 2016-01-17
 */
public interface CheckerJob {
  /**
   * Checks for new releases and updates the database if new releases is found.
   *
   * @return The new releases found.
   */
  List<Release> checkAndUpdate(Project project) throws IllegalArgumentException;
}
