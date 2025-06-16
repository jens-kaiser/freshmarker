package org.freshmarker.core;

import ftl.Node;

public class ReduceException extends ProcessException {
    public ReduceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReduceException(String message) {
        super(message);
    }

    public ReduceException(String message, Node node) {
        super(message, node);
    }

    public ReduceException(String message, Node node, Throwable cause) {
        super(message, node, cause);
    }
}
