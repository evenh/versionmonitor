package net.evenh.versionmonitor.services;

import net.evenh.versionmonitor.models.Release;
import net.evenh.versionmonitor.models.projects.GitHubProject;

import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Constructs various projects based on hosts.
 *
 * @since 2016-03-07
 */
@Service("projectBuilder")
public class ProjectBuilderService {
  private static final Logger logger = LoggerFactory.getLogger(ProjectBuilderService.class);

  @Autowired
  GitHubService gitHubService;

  /**
   * Constructs a new software project hosted on GitHub.
   *
   * @param identifier A username and project in this form: <code>apple/swift</code> for describing
   *                   the repository located at https://github.com/apple/swift
   * @throws FileNotFoundException Thrown if the project does not exist.
   */
  public Optional<GitHubProject> gitHub(String identifier) throws FileNotFoundException,
                                                                  NullPointerException {
    Objects.requireNonNull(identifier);

    try {
      final GitHubProject project = new GitHubProject();

      gitHubService.getRepository(identifier).ifPresent(repository -> {
        project.setIdentifier(identifier);
        project.setName(repository.getName());
        project.setDescription(repository.getDescription());
        project.setReleases(populateGitHubReleases(repository, identifier));
      });

      return Optional.of(project);
    } catch (IllegalArgumentException e) {
      logger.warn("Illegal arguments were supplied to the GitHubService", e);
    } catch (FileNotFoundException e) {
      logger.warn("Project not found: {}", identifier);
      throw e;
    } catch (Exception e) {
      logger.warn("Encountered problems using the GitHub service", e);
    }

    return Optional.empty();
  }

  /**
   * Processes a repository and creates domain releases.
   *
   * @param repository A GitHub repository
   * @param identifier The identifier with GitHub.
   * @return A list of releases, which may be empty
   */
  private List<Release> populateGitHubReleases(GHRepository repository, String identifier) {
    List<Release> releases = new ArrayList<>();

    try {
      Release.ReleaseBuilder mapper = Release.ReleaseBuilder.builder();

      releases = repository.listTags().asList()
              .stream()
              .map(tag -> mapper.fromGitHub(tag, identifier).build())
              .collect(Collectors.toList());
    } catch (IOException e) {
      logger.warn("Encountered IOException while populating GitHub releases", e);
    }

    return releases;
  }
}
