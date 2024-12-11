package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.WrongTypeException;

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
        throw new WrongTypeException("expected " + type.getSimpleName() + " but is " + result.getClass().getSimpleName() + " (" + result + ")");
    }

    default Class<?> getModelType() {
        return getClass();
    }

    default TemplateObject operation(TokenType operator, TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported operation: " + operator);
    }

    default boolean relation(TokenType operator, TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported operation: " + operator);
    }

    default TemplateObject negate() {
        throw new ProcessException("unsupported operation: negate");
    }
}
