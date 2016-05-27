package net.evenh.versionmonitor.domain.projects;

import net.evenh.versionmonitor.application.projects.AbstractProject;
import net.evenh.versionmonitor.application.projects.Project;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Implements a GitHub software project.
 *
 * @author Even Holthe
 * @since 2016-01-09
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
