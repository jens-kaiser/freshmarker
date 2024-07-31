package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public final class TemplateNull implements TemplateObject {

    public static final TemplateNull NULL = new TemplateNull();
    public static final TemplateNull NULL_LITERAL = new TemplateNull();

    private TemplateNull() {
        super();
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
}
