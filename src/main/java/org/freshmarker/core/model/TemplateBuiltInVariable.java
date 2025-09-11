package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

public record TemplateBuiltInVariable(String name) implements TemplateExpression {

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        return context.getBaseEnvironment().getBuiltInVariableProviders().provide(name, context);
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        return context.getBaseEnvironment().getBuiltInVariableProviders().provide(name, context);
    }
}

