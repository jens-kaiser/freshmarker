package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class TemplateNegative implements TemplateBooleanExpression {

    private final TemplateObject expression;

    public TemplateNegative(TemplateObject expression) {
        this.expression = expression;
    }

    @Override
    public TemplateObject not() {
        return expression;
    }

    @Override
    public TemplateBoolean evaluateToObject(ProcessContext context) {
        return expression.evaluate(context, TemplateBoolean.class).not();
    }

    @Override
    public void accept(TemplateObjectVisitor visitor) {
        visitor.visit(this, expression);
    }
}
