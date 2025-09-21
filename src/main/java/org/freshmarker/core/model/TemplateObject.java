package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateOperation.Operator;
import org.freshmarker.core.model.TemplateRelational.Relation;

public interface TemplateObject {
    default boolean isPrimitive() {
        return false;
    }

    default boolean isNull() {
        return false;
    }

    TemplateObject evaluateToObject(ProcessContext context);

    default <T extends TemplateObject> T evaluate(ProcessContext context, Class<T> type) {
        TemplateObject result = evaluateToObject(context);
        if (type.isInstance(result)) {
            return type.cast(result);
        }
        context.reductionCheck(result);
        throw new WrongTypeException("expected " + type.getSimpleName() + " but is " + result.getClass().getSimpleName() + " (" + result + ")");
    }

    default Class<?> getModelType() {
        return getClass();
    }

    @Deprecated
    default TemplateObject operation(TokenType operator, TemplateObject operand, ProcessContext context) {
        return switch (operator) {
            case PLUS -> operation(Operator.PLUS, operand, context);
            case MINUS -> operation(Operator.MINUS, operand, context);
            case TIMES -> operation(Operator.MULTIPLY, operand, context);
            case DIVIDE -> operation(Operator.DIVIDE, operand, context);
            case PERCENT -> operation(Operator.MODULO, operand, context);
            case CONCAT -> operation(Operator.CONCAT, operand, context);
            default -> throw new ProcessException("unsupported operation: " + operator);
        };
    }

    default TemplateObject operation(Operator operator, TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported operation: " + operator);
    }

    @Deprecated
    default boolean relation(TokenType operator, TemplateObject operand, ProcessContext context) {
        return switch (operator) {
            case LT -> relation(Relation.LT, operand, context);
            case GT -> relation(Relation.GT, operand, context);
            case LTE -> relation(Relation.LTE, operand, context);
            case GTE, UNICODE_GTE -> relation(Relation.GTE, operand, context);
            case COMPARE -> relation(Relation.COMPARE, operand, context);
            default -> throw new ProcessException("unsupported relation: " + operator);
        };
    }

    default boolean relation(Relation operator, TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported relation: " + operator);
    }

    default boolean equality(TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported equality: " + TokenType.EQUALS);
    }

    default TemplateObject negate() {
        throw new ProcessException("unsupported operation: negate");
    }

    default <R> R accept(TemplateObjectVisitor<R> visitor) {
        return null;
    }

    default TemplateObject reduce(ReduceContext context) {
        return this;
    }
}
