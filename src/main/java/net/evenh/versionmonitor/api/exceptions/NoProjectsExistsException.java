package net.evenh.versionmonitor.api.exceptions;

import org.springframework.http.HttpStatus;

public class NoProjectsExistsException extends VersionmonitorException {
  public NoProjectsExistsException() {
    super(HttpStatus.NOT_FOUND);
  }
}
