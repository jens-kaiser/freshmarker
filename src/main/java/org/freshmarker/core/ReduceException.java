package org.freshmarker.core;

import ftl.Node;

public class ReduceException extends RuntimeException {

    public ReduceException(String message) {
        super(message);
    }

    public ReduceException(String message, Node node) {
        super(message + " at " + generateLocation(node));
    }

    public ReduceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReduceException(String message, Node node, Throwable cause) {
        super(message + " at " + generateLocation(node), cause);
    }

    private static String generateLocation(Node node) {
        return node.getLocation() + " '" + node.getSource() + "'";
    }
}
