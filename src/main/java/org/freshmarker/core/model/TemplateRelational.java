package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public record TemplateRelational(TokenType type, TemplateObject left, TemplateObject right) implements TemplateBooleanExpression {

    @Override
    public TemplateBoolean evaluateToObject(ProcessContext context) {
        return TemplateBoolean.from(left.evaluateToObject(context).relation(type, right.evaluateToObject(context), context));
    }

    @Override
    public TemplateRelational not() {
        return switch (type) {
            case LT -> new TemplateRelational(TokenType.GTE, left, right);
            case GT -> new TemplateRelational(TokenType.LTE, left, right);
            case LTE -> new TemplateRelational(TokenType.GT, left, right);
            case GTE -> new TemplateRelational(TokenType.LT, left, right);
            default -> throw new IllegalArgumentException("unsupported relation: " + type);
        };
    }

    @Override
    public void accept(TemplateObjectVisitor visitor) {
        visitor.visit(this);
    }
}

