package net.evenh.versionmonitor.domain.projects;

import java.util.Optional;
import net.evenh.versionmonitor.domain.releases.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
  @Query("select p from Project p inner join p.releases r where r = :release")
  Optional<Project> findByRelease(@Param(value = "release") Release release);
}
