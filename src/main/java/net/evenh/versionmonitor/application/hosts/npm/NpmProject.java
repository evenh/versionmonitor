package net.evenh.versionmonitor.application.hosts.npm;

import javax.persistence.Entity;
import javax.persistence.Table;
import net.evenh.versionmonitor.domain.projects.Project;

/**
 * Represents a NPM software project.
 */
@Entity
@Table(name = "project_npm")
public class NpmProject extends Project {
  @Override
  public String getProjectUrl() {
    return "https://www.npmjs.com/package/" + getIdentifier();
  }
}
