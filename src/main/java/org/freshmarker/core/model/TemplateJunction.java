package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public record TemplateJunction(TokenType type, TemplateObject left, TemplateObject right) implements TemplateBooleanExpression {

    @Override
    public TemplateBoolean evaluateToObject(ProcessContext context) {
        TemplateBoolean leftValue = left.evaluate(context, TemplateBoolean.class);
        return TemplateBoolean.from(switch (type) {
            case AND -> right.evaluate(context, TemplateBoolean.class).getValue() && leftValue.getValue();
            case AND2 -> leftValue.getValue() && right.evaluate(context, TemplateBoolean.class).getValue();
            case OR -> right.evaluate(context, TemplateBoolean.class).getValue() || leftValue.getValue();
            case OR2 -> leftValue.getValue() || right.evaluate(context, TemplateBoolean.class).getValue();
            case XOR -> leftValue.getValue() ^ right.evaluate(context, TemplateBoolean.class).getValue();
            default -> throw new IllegalArgumentException("unsupported junction: " + type);
        });
    }

    @Override
    public TemplateBooleanExpression not() {
        TemplateObject newLeft = left instanceof TemplateBooleanExpression leftExpression ? leftExpression.not() : left;
        return switch (type) {
            case AND -> new TemplateJunction(TokenType.OR, newLeft, right instanceof TemplateBooleanExpression r ? r.not() : right);
            case OR -> new TemplateJunction(TokenType.AND, newLeft, right instanceof TemplateBooleanExpression r ? r.not() : right);
            case AND2 -> new TemplateJunction(TokenType.OR2, newLeft, new TemplateNegative(right));
            case OR2 -> new TemplateJunction(TokenType.AND2, newLeft, new TemplateNegative(right));
            case XOR -> new TemplateNegative(this);
            default -> throw new IllegalArgumentException("unsupported junction: " + type);
        };
    }

    public void accept(TemplateObjectVisitor visitor) {
        visitor.visit(this);
    }
}
