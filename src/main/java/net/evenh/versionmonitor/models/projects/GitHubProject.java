package net.evenh.versionmonitor.models.projects;

import net.evenh.versionmonitor.models.Project;
import net.evenh.versionmonitor.models.Release;
import net.evenh.versionmonitor.services.GitHubService;

import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Implements a GitHub software project.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
@Component
@Entity
@Table(name = "projects_github")
public class GitHubProject extends AbstractProject implements Project {
  private static final Logger logger = LoggerFactory.getLogger(GitHubProject.class);

  @Transient
  private GitHubService service;

  public GitHubProject(){
  }

  /**
   * Constructs a new software project hosted on GitHub.
   *
   * @param identifier  A username and project in this form: <code>apple/swift</code> for
   *                    describing the repository located at https://github.com/apple/swift
   * @throws FileNotFoundException Thrown if the project does not exist.
   */
  public GitHubProject(String identifier) throws FileNotFoundException {
    super(identifier);
    service = GitHubService.getInstance();

    try {
      // Fetch repository using GitHubService and unwrap the optional if present
      service.getRepository(getIdentifier()).ifPresent(repository -> {
        setName(repository.getName());
        setDescription(repository.getDescription());
        setReleases(populateReleases(repository));
      });
    } catch (IllegalArgumentException e) {
      logger.warn("Illegal arguments were supplied to the GitHubService", e);
    } catch (FileNotFoundException e) {
      logger.warn("Project not found: {}", identifier);
      throw e;
    } catch (Exception e) {
      logger.warn("Encountered problems using the GitHub service", e);
    }
  }

  /**
   * Processes a repository and creates domain releases.
   *
   * @param repository A GitHub repository
   * @return A list of releases, which may be empty
   */
  private List<Release> populateReleases(GHRepository repository) {
    List<Release> releases = new ArrayList<>();

    try {
      Release.ReleaseBuilder mapper = Release.ReleaseBuilder.builder();

      releases = repository.listTags().asList()
              .stream()
              .map(tag -> mapper.fromGitHub(tag, getIdentifier()).build())
              .collect(Collectors.toList());
    } catch (IOException e) {
      logger.warn("Encountered IOException while populating GitHub releases", e);
    }

    return releases;
  }
}
