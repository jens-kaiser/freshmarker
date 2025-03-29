package org.freshmarker.core.environment;

public record NameSpaced(String namespace, String name) {
    public NameSpaced(String name) {
        this(null, name);
    }
}
