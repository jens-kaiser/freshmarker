package org.freshmarker.core;

import ftl.Node;

public class UnsupportedBuiltInException extends ProcessException {

  public UnsupportedBuiltInException(String message) {
    super(message);
  }

  public UnsupportedBuiltInException(String message, Node node, Throwable cause) {
    super(message, node, cause);
  }
}
