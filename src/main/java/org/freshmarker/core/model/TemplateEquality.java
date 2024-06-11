package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public record TemplateEquality(TemplateObject left, TemplateObject right) implements TemplateBooleanExpression {

    public TemplateNegative not() {
        return new TemplateNegative(this);
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject leftObject = left.evaluateToObject(context);
        TemplateObject rightObject = right.evaluateToObject(context);
        return TemplateBoolean.from(leftObject.equals(rightObject));
    }
}
