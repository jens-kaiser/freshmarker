package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.Optional;

public interface TemplateObject {

    default boolean isPrimitive() {
        return false;
    }

    default boolean isMarkup() {
        return false;
    }

    default <T> Optional<TemplatePrimitive<T>> asPrimitive() {
        return Optional.empty();
    }

    default Optional<TemplateNumber> asNumber() {
        return Optional.empty();
    }

    default Optional<TemplateString> asString() {
        return Optional.empty();
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
}
