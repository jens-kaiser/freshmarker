package org.freshmarker.core;

import ftl.Node;

public class WrongTypeException extends ProcessException {

  public WrongTypeException(String message) {
    super(message);
  }

  public WrongTypeException(String message, Node node) {
    super(message, node);
  }

}
