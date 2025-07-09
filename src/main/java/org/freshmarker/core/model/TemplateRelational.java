package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public record TemplateRelational(TokenType type, TemplateObject left, TemplateObject right) implements TemplateBooleanExpression {

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplatePrimitive<?> leftPrimitive = left.evaluate(context, TemplatePrimitive.class);
        TemplatePrimitive<?> rightPrimitive = right.evaluate(context, TemplatePrimitive.class);
        return leftPrimitive.relational(type, rightPrimitive, context);
    }

    @Override
    public TemplateRelational not() {
        return switch (type) {
            case LT -> new TemplateRelational(TokenType.GTE, left, right);
            case GT -> new TemplateRelational(TokenType.LTE, left, right);
            case LTE -> new TemplateRelational(TokenType.GT, left, right);
            case GTE, UNICODE_GTE -> new TemplateRelational(TokenType.LT, left, right);
            default -> throw new ProcessException("unsupported relation: " + type);
        };
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

