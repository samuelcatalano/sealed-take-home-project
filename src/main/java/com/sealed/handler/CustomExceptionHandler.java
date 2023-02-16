package com.sealed.handler;

import jakarta.persistence.EntityNotFoundException;

import com.sealed.exception.ServiceException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handle {@link ServiceException} exceptions thrown by the application and return an error response
   * with HTTP status code 500 (INTERNAL_SERVER_ERROR).
   * @param ex the exception to handle
   * @param request the current request
   * @return an error response with HTTP status code 500 (INTERNAL_SERVER_ERROR)
   * @throws {@link ServiceException}
   * @ExceptionHandler Specifies the type of exception this method handles.
   */
  @ExceptionHandler(value = ServiceException.class)
  public ResponseEntity<ErrorResponse> handleServiceException(final ServiceException ex, final WebRequest request) {
    final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }

  /**
   * This method handles the EntityNotFoundException by returning a response with a corresponding error message.
   * @param ex the EntityNotFoundException that needs to be handled.
   * @param request the web request that resulted in the exception.
   * @return A ResponseEntity with a status code of HttpStatus.OK and a body of ErrorResponse with error information.
   */
  @ExceptionHandler(value = EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final EntityNotFoundException ex, final WebRequest request) {
    final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.name(), ex.getMessage(), HttpStatus.NOT_FOUND.value());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }
}
