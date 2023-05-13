package org.freshmarker.core.ftl;

import ftl.Node;

public class ParsingException extends RuntimeException {

  public ParsingException(String message, Node node) {
    super(message + " at " + generateLocation(node));
  }

  private static String generateLocation(Node node) {
    return node.getLocation() + " '" + node.getSource() + "'";
  }

}
