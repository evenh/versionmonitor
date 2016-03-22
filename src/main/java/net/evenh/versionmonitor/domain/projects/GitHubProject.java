package net.evenh.versionmonitor.domain.projects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger logger = LoggerFactory.getLogger(GitHubProject.class);

  public GitHubProject() {
  }

  @Override
  public String getProjectUrl() {
    return "https://github.com/" + getIdentifier();
  }
}
