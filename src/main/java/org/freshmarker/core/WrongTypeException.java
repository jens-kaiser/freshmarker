package org.freshmarker.core;

import ftl.Node;

public class WrongTypeException extends ReduceException {

  public WrongTypeException(String message) {
    super(message);
  }

  public WrongTypeException(String message, Node node) {
    super(message, node);
  }

  public WrongTypeException(String message, Node node, Throwable cause) {
    super(message, node, cause);
  }
}
