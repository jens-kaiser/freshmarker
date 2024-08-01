package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public class TemplateDefault implements TemplateExpression {
    private final TemplateObject base;
    private final TemplateObject fallback;

    public TemplateDefault(TemplateObject base, TemplateObject fallback) {
        this.base = base;
        this.fallback = fallback;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject templateObject = base.evaluateToObject(context);
        return context.reductionCheck(templateObject) ? templateObject : fallback.evaluateToObject(context);
    }
}
