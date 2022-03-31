package org.freshmarker.core.model;

import java.util.Optional;
import org.freshmarker.core.Environment;
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

  default Optional<TemplatePrimitive<?>> asPrimitive() {
    return Optional.empty();
  }

  default Optional<TemplateNumber> asNumber() {
    return Optional.empty();
  }

  default Optional<TemplateString> asString() {
    return Optional.empty();
  }

  default TemplateObject evaluateToObject(Environment environment) {
    return this;
  }
}
