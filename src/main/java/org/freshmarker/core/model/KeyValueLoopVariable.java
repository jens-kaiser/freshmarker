package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public record KeyValueLoopVariable(String name) implements TemplateVariable {

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        return context.getEnvironment().getValue(name).evaluateToObject(context);
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, name);
    }
}
