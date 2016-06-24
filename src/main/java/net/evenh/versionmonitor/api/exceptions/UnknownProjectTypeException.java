package net.evenh.versionmonitor.api.exceptions;

import org.springframework.http.HttpStatus;

public class UnknownProjectTypeException extends VersionmonitorException {
  public UnknownProjectTypeException() {
    super(HttpStatus.UNPROCESSABLE_ENTITY);
  }
}
