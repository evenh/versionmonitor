package net.evenh.versionmonitor.domain.projects.npm;

import net.evenh.versionmonitor.application.projects.AbstractProject;
import net.evenh.versionmonitor.application.projects.Project;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents a NPM software project.
 */
@Entity
@Table(name = "projects_npm")
public class NpmProject extends AbstractProject implements Project {
  public NpmProject() {
  }

  @Override
  public String getProjectUrl() {
    return "https://www.npmjs.com/package/" + getIdentifier();
  }
}
