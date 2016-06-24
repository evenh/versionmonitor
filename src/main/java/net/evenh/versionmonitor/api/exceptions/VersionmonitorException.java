package net.evenh.versionmonitor.api.exceptions;

import org.springframework.http.HttpStatus;

public class VersionmonitorException extends RuntimeException {
  private HttpStatus statusCode;

  public VersionmonitorException() {
    this(HttpStatus.BAD_REQUEST);
  }

  public VersionmonitorException(HttpStatus status) {
    this.statusCode = status;
  }

  public HttpStatus getStatusCode() {
    return statusCode;
  }

  public static String errorCode(Class<? extends VersionmonitorException> clazz) {
    return format(clazz.getSimpleName().replace("Exception", ""));
  }

  private static String format(String str) {
    final StringBuilder result = new StringBuilder();

    for (int i = 0; i < str.length(); i++) {
      final char c = str.charAt(i);
      if (i > 0 && Character.isUpperCase(c)) {
        result.append('_');
      }
      result.append(c);
    }

    return result.toString().toLowerCase();
  }
}
