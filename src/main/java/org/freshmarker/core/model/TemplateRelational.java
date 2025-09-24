package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public record TemplateRelational(Relation type, TemplateObject left, TemplateObject right) implements TemplateBooleanExpression {
    public enum Relation {
        LT, GT, LTE, GTE, COMPARE
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplatePrimitive<?> leftPrimitive = left.evaluate(context, TemplatePrimitive.class);
        TemplatePrimitive<?> rightPrimitive = right.evaluate(context, TemplatePrimitive.class);
        return leftPrimitive.relational(type, rightPrimitive, context);
    }

    @Override
    public TemplateRelational not() {
        return switch (type) {
            case LT -> new TemplateRelational(Relation.GTE, left, right);
            case GT -> new TemplateRelational(Relation.LTE, left, right);
            case LTE -> new TemplateRelational(Relation.GT, left, right);
            case GTE -> new TemplateRelational(Relation.LT, left, right);
            case COMPARE -> new TemplateRelational(Relation.COMPARE, right, left);
        };
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        TemplateObject leftObject = left.reduce(context);
        TemplateObject rightObject = right.reduce(context);
        if (leftObject instanceof TemplatePrimitive<?> leftPrimitive && rightObject instanceof TemplatePrimitive<?> rightPrimitive) {
            TemplatePrimitive<?> result = leftPrimitive.relational(type, rightPrimitive, context);
            context.getStatus().expression().addAndGet(2);
            return result;
        }
        return new TemplateRelational(type, leftObject, rightObject);
    }
}

