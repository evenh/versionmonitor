package net.evenh.versionmonitor.api.exceptions;

import org.springframework.http.HttpStatus;

public class SubscriptionNotFoundException extends VersionmonitorException {
  public SubscriptionNotFoundException() {
    super(HttpStatus.NOT_FOUND);
  }
}
