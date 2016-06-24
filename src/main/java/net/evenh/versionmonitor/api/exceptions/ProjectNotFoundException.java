package net.evenh.versionmonitor.api.exceptions;

import org.springframework.http.HttpStatus;

public class ProjectNotFoundException extends VersionmonitorException {
  public ProjectNotFoundException() {
    super(HttpStatus.NOT_FOUND);
  }
}
