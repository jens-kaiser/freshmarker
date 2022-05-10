package org.freshmarker.core;

import ftl.Node;

public class ProcessException extends RuntimeException {
  public ProcessException(String message) {
    super(message);
  }

  public ProcessException(String message, Node node) {
    super(message + " at " + generateLocation(node));
  }

  public ProcessException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProcessException(String message, Node node, Throwable cause) {
    super(message + " at " + generateLocation(node), cause);
  }

  private static String generateLocation(Node node) {
    return node.getLocation() + " '" + node.getSource() + "'";
  }
}
