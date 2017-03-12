package net.evenh.versionmonitor.application.hosts.github;

import javax.persistence.Entity;
import javax.persistence.Table;
import net.evenh.versionmonitor.domain.projects.Project;

/**
 * Represents a GitHub software project.
 */
@Entity
@Table(name = "project_github")
public class GitHubProject extends Project {
  public GitHubProject() {
  }

  @Override
  public String getProjectUrl() {
    return "https://github.com/" + getIdentifier();
  }
}
