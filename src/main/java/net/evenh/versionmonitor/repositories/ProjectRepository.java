package net.evenh.versionmonitor.repositories;

import net.evenh.versionmonitor.models.projects.AbstractProject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * A repository for persisting various software projects.
 *
 * @author Even Holthe
 * @since 2016-01-10
 */
public interface ProjectRepository extends JpaRepository<AbstractProject, Long> {
  Optional<AbstractProject> findByIdentifier(String identifier);
}
