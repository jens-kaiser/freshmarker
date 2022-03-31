package org.freshmarker.core;

public class WrongTypeException extends ProcessException {

  public WrongTypeException(String message) {
    super(message);
  }

  public WrongTypeException(String message, Throwable cause) {
    super(message, cause);
  }
}
