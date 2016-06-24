package net.evenh.versionmonitor.api.exceptions;

import org.springframework.http.HttpStatus;

public class SubscriptionAlreadyLinkedToProjectException extends VersionmonitorException {
  public SubscriptionAlreadyLinkedToProjectException() {
    super(HttpStatus.CONFLICT);
  }
}
