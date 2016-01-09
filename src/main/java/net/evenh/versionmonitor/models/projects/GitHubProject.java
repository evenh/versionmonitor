package net.evenh.versionmonitor.models.projects;

import net.evenh.versionmonitor.models.Release;
import net.evenh.versionmonitor.services.GitHubService;

import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implements a GitHub software project.
 *
 * @author Even Holthe
 * @since 2016-01-09
 */
@Component
public class GitHubProject extends AbstractProject {
  private static final Logger logger = LoggerFactory.getLogger(GitHubProject.class);

  private GitHubService service;

  /**
   * Constructs a new software project hosted on GitHub.
   *
   * @param identifier  A username and project in this form: <code>apple/swift</code> for
   *                    describing the repository located at https://github.com/apple/swift
   */
  public GitHubProject(String identifier) {
    super(identifier);
    service = GitHubService.getInstance();

    try {
      // Fetch repository using GitHubService.
      Optional<GHRepository> repo = service.getRepository(getIdentifier());

      // Unwrap the optional if present
      if (repo.isPresent()) {
        GHRepository repository = repo.get();

        // Populate the data object
        this.setName(repository.getName());
        this.setDescription(repository.getDescription());
        this.setReleases(populateReleases(repository));
      }
    } catch (IllegalArgumentException e) {
      logger.warn("Illegal arguments were supplied to the GitHubService", e);
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
