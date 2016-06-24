package net.evenh.versionmonitor.api.exceptions;

import org.springframework.http.HttpStatus;

public class UnknownSubscriptionServiceException extends VersionmonitorException {
  public UnknownSubscriptionServiceException() {
    super(HttpStatus.BAD_REQUEST);
  }
}
