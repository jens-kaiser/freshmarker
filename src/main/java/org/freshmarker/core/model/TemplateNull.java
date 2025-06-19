package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public final class TemplateNull implements TemplateObject {

    public static final TemplateNull NULL = new TemplateNull("NULL");
    public static final TemplateNull NULL_LITERAL = new TemplateNull("NULL_LITERAL");
    public static final TemplateNull NULL_OPTIONAL = new TemplateNull("NULL_OPTIONAL");

    private final String name;

    private TemplateNull(String name) {
        this.name = name;
    }

    @Override
    public TemplateNull evaluateToObject(ProcessContext context) {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TemplateNull;
    }

    @Override
    public int hashCode() {
        return 23;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    public String toString() {
        return name;
    }
}
