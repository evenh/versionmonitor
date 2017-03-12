package net.evenh.versionmonitor.application.hosts.npm;

import net.evenh.versionmonitor.domain.projects.AbstractProject;
import net.evenh.versionmonitor.domain.projects.Project;

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
