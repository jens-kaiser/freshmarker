package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

public record TemplateDefault(TemplateObject base, TemplateObject fallback) implements TemplateExpression {

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject templateObject = base.evaluateToObject(context);
        return context.reductionCheck(templateObject) ? templateObject : fallback.evaluateToObject(context);
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        TemplateObject templateObject = base.reduce(context);
        return templateObject.isNull() ? this : templateObject;
    }
}
