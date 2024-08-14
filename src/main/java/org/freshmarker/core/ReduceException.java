package org.freshmarker.core;

public class ReduceException extends RuntimeException {
    public ReduceException(String message) {
        super(message);
    }

    public ReduceException(String message, Throwable cause) {
        super(message, cause);
    }
}
