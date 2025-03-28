package org.freshmarker.core.buildin;

import org.freshmarker.core.model.TemplateObject;

import java.util.Objects;

/**
 * Key used for the internal management of built-ins.
 */
public final class BuiltInKey {
    private final Class<? extends TemplateObject> type;
    private final String name;
    private final int hash;

    public BuiltInKey(Class<? extends TemplateObject> type, String name) {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        hash = Objects.hash(type, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BuiltInKey that) {
            return type.equals(that.type) && name.equals(that.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
