package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public record TemplateEquality(TemplateObject left, TemplateObject right) implements TemplateBooleanExpression {

    public TemplateNegative not() {
        return new TemplateNegative(this);
    }

    @Override
    public TemplateBoolean evaluateToObject(ProcessContext context) {
        TemplateObject leftObject = evaluate(left, context);
        TemplateObject rightObject = evaluate(right, context);
        if (leftObject == TemplateNull.NULL && rightObject != TemplateNull.NULL_LITERAL ||
                leftObject != TemplateNull.NULL_LITERAL && rightObject == TemplateNull.NULL) {
            throw new ProcessException("null compare only allowed with null literal");
        }
        return TemplateBoolean.from(leftObject.equals(rightObject));
    }

    private TemplateObject evaluate(TemplateObject object, ProcessContext context) {
        TemplateObject result = object.evaluateToObject(context);
        if (result.isPrimitive() || result.isNull()) {
            return result;
        }
        throw new ProcessException("invalid type " + result.getModelType());
    }
}
