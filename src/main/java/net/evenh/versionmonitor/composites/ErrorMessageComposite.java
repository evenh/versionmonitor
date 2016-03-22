package net.evenh.versionmonitor.composites;

import net.evenh.versionmonitor.domain.ErrorCode;

import java.io.Serializable;

/**
 * A composite to serialize <code>ErrorCode</code> objects.
 *
 * @author Even Holthe
 * @since 2016-01-17
 */
public class ErrorMessageComposite implements Serializable {
  private Integer errorCode;
  private String message;

  public Integer getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }

  private void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  private void setMessage(String message) {
    this.message = message;
  }

  /**
   * Convenience builder method to populate an <code>ErrorMessageComposite</code> from an
   * <code>ErrorCode</code> object.
   *
   * @param errorCode A <code>ErrorCode</code> object.
   * @return A populated <code>ErrorMessageComposite</code> object.
   */
  public static ErrorMessageComposite of(ErrorCode errorCode) {
    ErrorMessageComposite error = new ErrorMessageComposite();

    error.setErrorCode(errorCode.getErrorCode());
    error.setMessage(errorCode.getErrorMessage());

    return error;
  }
}
