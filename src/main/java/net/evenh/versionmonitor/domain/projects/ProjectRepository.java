package net.evenh.versionmonitor.domain.projects;

import net.evenh.versionmonitor.domain.releases.Release;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A repository for persisting various software projects.
 *
 * @author Even Holthe
 * @since 2016-01-10
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
  Optional<Project> findByIdentifier(String identifier);

  /**
   * Finds a project by supplying a release.
   *
   * @param release A <code>Release</code> object.
   * @return An Optional<code>Project</code> for describing whether a project was found or
   *         not.
   */
  @Query("select a from Project a inner join a.releases r where r = :r")
  Optional<Project> findByRelease(@Param(value = "r") Release release);
}
