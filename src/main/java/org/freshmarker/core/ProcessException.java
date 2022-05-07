package org.freshmarker.core;

import ftl.Node;

public class ProcessException extends RuntimeException {

  public ProcessException(String message) {
    super(message);
  }

  public ProcessException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProcessException(String message, Node node, Throwable cause) {
    super(message + " in " + generateLocation(node), cause);
  }

  private static String generateLocation(Node node) {
    if (node.getBeginLine() != node.getEndLine()) {
      return "lines " + node.getBeginLine() + "..." + node.getEndLine() + " '" + node.getSource() + "'";
    }
    return "line " + node.getBeginLine() + " column " + node.getBeginColumn() + " '" + node.getSource() + "'";
  }
}
