package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public record TemplateDefault(TemplateObject base, TemplateObject fallback) implements TemplateExpression {

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject templateObject = base.evaluateToObject(context);
        return context.reductionCheck(templateObject) ? templateObject : fallback.evaluateToObject(context);
    }

    @Override
    public void accept(TemplateObjectVisitor visitor) {
        visitor.visit(this);
    }
}
