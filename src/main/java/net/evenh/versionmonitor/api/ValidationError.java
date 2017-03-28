package net.evenh.versionmonitor.api;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ValidationError {
  public List<FieldValidationError> errors;

  /**
   * Construct a validation error from a {@link BindingResult} provided by Spring.
   */
  public ValidationError(BindingResult result) {
    this(result.getFieldErrors().stream()
      .map(FieldValidationError::new)
      .collect(Collectors.toList()));
  }

  public ValidationError(List<FieldValidationError> errors) {
    this.errors = errors;
  }

  private ValidationError() {
  }

  private static final class FieldValidationError {
    public String field;
    public String code;
    public String message;

    public FieldValidationError(String field, String code, String message) {
      this.field = field;
      this.code = code;
      this.message = message;
    }

    public FieldValidationError(FieldError error) {
      this(error.getField(), error.getCode(), error.getDefaultMessage());
    }

    private FieldValidationError() {
    }
  }
}
