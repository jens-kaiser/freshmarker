package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public record KeyValueLoopVariable(String name) implements TemplateVariable {

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject value = context.getEnvironment().getValue(name);
        return value.evaluateToObject(context);
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, name);
    }
}
