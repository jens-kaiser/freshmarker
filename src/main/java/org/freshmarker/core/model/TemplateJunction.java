package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class TemplateJunction implements TemplateBooleanExpression {
    private final TokenType type;
    private final TemplateObject left;
    private final TemplateObject right;

    public TemplateJunction(TokenType type, TemplateObject left, TemplateObject right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateBoolean leftValue = left.evaluate(context, TemplateBoolean.class);
        return TemplateBoolean.from(switch (type) {
            case AND -> leftValue.getValue() & right.evaluate(context, TemplateBoolean.class).getValue();
            case AND2 -> leftValue.getValue() && right.evaluate(context, TemplateBoolean.class).getValue();
            case OR -> leftValue.getValue() | right.evaluate(context, TemplateBoolean.class).getValue();
            case OR2 -> leftValue.getValue() || right.evaluate(context, TemplateBoolean.class).getValue();
            case XOR -> leftValue.getValue() ^ right.evaluate(context, TemplateBoolean.class).getValue();
            default -> throw new IllegalArgumentException("unsupported junction: " + type);
        });
    }

    @Override
    public TemplateObject not() {
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
}
