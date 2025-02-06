package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public record TemplateBuiltInVariable(String name) implements TemplateExpression {

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        return context.getBaseEnvironment().getBuiltInVariableProviders().provide(name, context);
    }

    @Override
    public void accept(TemplateObjectVisitor visitor) {
        visitor.visit(this);
    }
}

