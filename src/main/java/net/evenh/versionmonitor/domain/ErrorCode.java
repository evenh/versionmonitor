package net.evenh.versionmonitor.domain;

import org.springframework.http.HttpStatus;

/**
 * Different error codes used throughout the system.
 *
 * @author Even Holthe
 * @since 2016-01-17
 */
@SuppressWarnings("CheckStyle")
public enum ErrorCode {
  PROJECT_NOT_FOUND
          (1, "The requested project was not found.", HttpStatus.NOT_FOUND),
  ERROR_CREATING_PROJECT
          (2, "Could not create project. Try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
  NO_PROJECTS
          (3,  "No projects exists yet.", HttpStatus.NOT_FOUND),
  UNKNOWN_PROJECT_TYPE
          (4, "Unknown project host specified.", HttpStatus.BAD_REQUEST),
  DUPLICATE_PROJECT
          (5, "This project already exists.", HttpStatus.CONFLICT),
  HOST_UNKNOWN_PROJECT
          (6, "The host doesn't know the specified project.", HttpStatus.NOT_FOUND);

  private int errorCode;
  private String errorMessage;
  private HttpStatus httpStatus;

  ErrorCode(int errorCode, String errorMessage, HttpStatus httpStatus) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.httpStatus = httpStatus;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
