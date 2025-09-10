package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public record TemplateEquality(TemplateObject left, TemplateObject right) implements TemplateBooleanExpression {

    public TemplateNot not() {
        return new TemplateNot(this);
    }

    @Override
    public TemplateBoolean evaluateToObject(ProcessContext context) {
        TemplateObject leftObject = evaluate(left, context);
        TemplateObject rightObject = evaluate(right, context);
        if (isNonLiteralNull(leftObject) && rightObject != TemplateNull.NULL_LITERAL ||
            leftObject != TemplateNull.NULL_LITERAL && isNonLiteralNull(rightObject)) {
            throw new ProcessException("null compare only allowed with null literal");
        }
        return TemplateBoolean.from(leftObject.equality(rightObject, context));
    }

    private static boolean isNonLiteralNull(TemplateObject object) {
        return object == TemplateNull.NULL || object == TemplateNull.NULL_OPTIONAL;
    }

    private TemplateObject evaluate(TemplateObject object, ProcessContext context) {
        TemplateObject result = object.evaluateToObject(context);
        if (result.isPrimitive() || result.isNull()) {
            return result;
        }
        throw new ProcessException("invalid type " + result.getModelType());
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, left, right);
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        return new TemplateEquality(left.reduce(context), right.reduce(context));
    }
}
