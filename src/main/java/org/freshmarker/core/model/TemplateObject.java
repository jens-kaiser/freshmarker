package org.freshmarker.core.model;

import java.util.Optional;
import org.freshmarker.core.Environment;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.freshmarker.core.model.primitive.TemplateString;

public interface TemplateObject {

  default boolean isNumber() {
    return false;
  }

  default boolean isPrimitive() {
    return false;
  }

  default boolean isMarkup() {
    return false;
  }

  default Optional<TemplatePrimitive<?>> asPrimitive() {
    return Optional.empty();
  }

  default Optional<TemplateNumber> asNumber() {
    return Optional.empty();
  }

  default Optional<TemplateString> asString() {
    return Optional.empty();
  }

  TemplateObject evaluateToObject(Environment environment);

  default <T extends TemplateObject> T evaluate(Environment environment, Class<T> type) {
    TemplateObject result = evaluateToObject(environment);
    if (type.isInstance(result)) {
      return type.cast(result);
    }
    throw new WrongTypeException("expected " + type + " but is " + result.getClass() + " (" + result + ")");
  }
}
