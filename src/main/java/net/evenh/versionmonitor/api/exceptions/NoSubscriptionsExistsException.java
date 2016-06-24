package net.evenh.versionmonitor.api.exceptions;

import org.springframework.http.HttpStatus;

public class NoSubscriptionsExistsException extends VersionmonitorException {
  public NoSubscriptionsExistsException() {
    super(HttpStatus.NOT_FOUND);
  }
}
