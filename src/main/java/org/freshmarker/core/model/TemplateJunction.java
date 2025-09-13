package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
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
            default -> throw new ProcessException("unsupported junction: " + type);
        });
    }

    @Override
    public TemplateBooleanExpression not() {
        TemplateObject newLeft = left instanceof TemplateBooleanExpression leftExpression ? leftExpression.not() : left;
        return switch (type) {
            case AND -> new TemplateJunction(TokenType.OR, newLeft, right instanceof TemplateBooleanExpression r ? r.not() : right);
            case OR -> new TemplateJunction(TokenType.AND, newLeft, right instanceof TemplateBooleanExpression r ? r.not() : right);
            case AND2 -> new TemplateJunction(TokenType.OR2, newLeft, new TemplateNot(right));
            case OR2 -> new TemplateJunction(TokenType.AND2, newLeft, new TemplateNot(right));
            case XOR -> new TemplateNot(this);
            default -> throw new ProcessException("unsupported junction: " + type);
        };
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        TemplateObject leftValue = left.reduce(context);
        return switch (type) {
            case AND -> reduceAnd(context, leftValue);
            case AND2 -> reduceAnd2(context, leftValue);
            case OR -> reduceOr(context, leftValue);
            case OR2 -> reduceOr2(context, leftValue);
            case XOR -> reduceXor(context, leftValue);
            default -> throw new ProcessException("unsupported junction: " + type);
        };
    }

    private TemplateObject reduceXor(ReduceContext context, TemplateObject leftValue) {
        TemplateObject rightValue = right.reduce(context);
        if (leftValue instanceof TemplateBoolean leftBoolean && rightValue instanceof TemplateBoolean rightBoolean) {
            context.getStatus().expression().addAndGet(2);
            return TemplateBoolean.from(leftBoolean.getValue() ^ rightBoolean.getValue());
        }
        return new TemplateJunction(type, leftValue, rightValue);
    }

    private TemplateObject reduceOr2(ReduceContext context, TemplateObject leftValue) {
        if (TemplateBoolean.TRUE.equals(leftValue)) {
            context.getStatus().expression().addAndGet(2);
            return TemplateBoolean.TRUE;
        }
        TemplateObject rightValue = right.reduce(context);
        if (TemplateBoolean.FALSE.equals(leftValue)) {
            context.getStatus().expression().addAndGet(2);
            return rightValue;
        }
        if (TemplateBoolean.FALSE.equals(rightValue)) {
            context.getStatus().expression().addAndGet(2);
            return leftValue;
        }
        return new TemplateJunction(type, leftValue, rightValue);
    }


    private TemplateObject reduceOr(ReduceContext context, TemplateObject leftValue) {
        TemplateObject rightValue = right.reduce(context);
        if (rightValue instanceof TemplateBoolean rightBoolean && leftValue instanceof TemplateBoolean leftBoolean) {
            context.getStatus().expression().addAndGet(2);
            return TemplateBoolean.from(rightBoolean.getValue() || leftBoolean.getValue());
        }
        if (TemplateBoolean.FALSE.equals(leftValue)) {
            context.getStatus().expression().addAndGet(2);
            return rightValue;
        }
        if (TemplateBoolean.FALSE.equals(rightValue)) {
            context.getStatus().expression().addAndGet(2);
            return leftValue;
        }
        return new TemplateJunction(type, left.reduce(context), right.reduce(context));
    }

    private TemplateObject reduceAnd2(ReduceContext context, TemplateObject leftValue) {
        if (TemplateBoolean.FALSE.equals(leftValue)) {
            context.getStatus().expression().addAndGet(2);
            return TemplateBoolean.FALSE;
        }
        TemplateObject rightValue = right.reduce(context);
        if (TemplateBoolean.TRUE.equals(leftValue)) {
            context.getStatus().expression().addAndGet(2);
            return rightValue;
        }
        if (TemplateBoolean.TRUE.equals(rightValue)) {
            context.getStatus().expression().addAndGet(2);
            return leftValue;
        }
        return new TemplateJunction(type, leftValue, rightValue);
    }

    private TemplateObject reduceAnd(ReduceContext context, TemplateObject leftValue) {
        TemplateObject rightValue = right.reduce(context);
        if (rightValue instanceof TemplateBoolean rightBoolean && leftValue instanceof TemplateBoolean leftBoolean) {
            context.getStatus().expression().addAndGet(2);
            return TemplateBoolean.from(rightBoolean.getValue() && leftBoolean.getValue());
        }
        if (TemplateBoolean.TRUE.equals(leftValue)) {
            context.getStatus().expression().addAndGet(2);
            return rightValue;
        }
        if (TemplateBoolean.TRUE.equals(rightValue)) {
            context.getStatus().expression().addAndGet(2);
            return leftValue;
        }
        return new TemplateJunction(type, leftValue, rightValue);
    }
}
