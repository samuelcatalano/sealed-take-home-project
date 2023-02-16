package com.sealed.exception;

import java.io.Serial;

public class ServiceException extends Exception {

  @Serial
  private static final long serialVersionUID = 1L;

  public ServiceException() {
    super();
  }

  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public ServiceException(Throwable cause) {
    super(cause);
  }
}