package org.freshmarker.core.model.primitive;

import java.util.Optional;
import org.freshmarker.core.Environment;

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

  default String evaluate(Environment environment) {
    return environment.getFormatter(getClass()).format(this, environment.getLocale());
  }

  default TemplateObject evaluateToObject(Environment environment) {
    return this;
  }
}
