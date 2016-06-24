package net.evenh.versionmonitor.api.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateProjectException extends VersionmonitorException {
  public DuplicateProjectException() {
    super(HttpStatus.CONFLICT);
  }
}
