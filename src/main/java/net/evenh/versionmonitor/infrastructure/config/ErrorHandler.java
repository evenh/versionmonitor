package net.evenh.versionmonitor.infrastructure.config;

import com.google.common.collect.ImmutableMap;

import net.evenh.versionmonitor.api.ValidationError;
import net.evenh.versionmonitor.api.exceptions.VersionmonitorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
  private final static Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
  private final static String loggingTemplate = "{}: {} method={}, uri={}, client={}";

  @Autowired
  HttpServletRequest request;

  @ExceptionHandler
  public ResponseEntity<Void> handleUncaughtException(Exception e) {
    logWarning("internal server error", e);
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler
  public ResponseEntity<? extends Object> handleAppException(VersionmonitorException e) {
    logger.info(loggingTemplate,
      e.getClass().getSimpleName(),
      "[NO-MSG]",
      request.getMethod(),
      request.getRequestURI(),
      request.getRemoteAddr());

    return ResponseEntity.status(e.getStatusCode())
      .body(ImmutableMap.of("code", VersionmonitorException.errorCode(e.getClass()), "timestamp", new Date()));
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
    logWarning("unable to process request", ex);
    return super.handleExceptionInternal(ex, body, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    logWarning("validation error", ex);
    return new ResponseEntity<>(new ValidationError(ex.getBindingResult()), status);
  }

  private void logWarning(String message, Exception e) {
    logger.warn(loggingTemplate, e.getClass().getSimpleName(), message, request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), e);
  }
}
