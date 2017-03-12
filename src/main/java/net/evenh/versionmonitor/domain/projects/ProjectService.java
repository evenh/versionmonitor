package net.evenh.versionmonitor.domain.projects;

import java.util.List;
import java.util.Optional;
import net.evenh.versionmonitor.domain.releases.Release;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
  @Autowired
  private ProjectRepository repository;

  /**
   * Finds all projects persisted in the database.
   */
  public List<AbstractProject> findAll() {
    return repository.findAll();
  }

  /**
   * Finds a project by id.
   */
  public Optional<AbstractProject> findOne(Long id) {
    return Optional.ofNullable(repository.findOne(id));
  }

  /**
   * Checks if a project exists.
   */
  public boolean doesExist(String identifier) {
    return repository.findByIdentifier(identifier).isPresent();
  }

  /**
   * Persists a project to database.
   */
  public AbstractProject persist(AbstractProject project) {
    return repository.saveAndFlush(project);
  }

  /**
   * Deletes a project.
   */
  public void delete(AbstractProject project) {
    repository.delete(project);
  }

  /**
   * Finds a project by a given release.
   */
  public Optional<AbstractProject> findByRelease(Release release) {
    return repository.findByRelease(release);
  }
}
