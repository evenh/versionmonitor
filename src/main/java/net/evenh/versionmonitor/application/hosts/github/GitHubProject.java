package net.evenh.versionmonitor.application.hosts.github;

import net.evenh.versionmonitor.domain.projects.AbstractProject;
import net.evenh.versionmonitor.domain.projects.Project;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents a GitHub software project.
 */
@Entity
@Table(name = "projects_github")
public class GitHubProject extends AbstractProject implements Project {
  public GitHubProject() {
  }

  @Override
  public String getProjectUrl() {
    return "https://github.com/" + getIdentifier();
  }
}
