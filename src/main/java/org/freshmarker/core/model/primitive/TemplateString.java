package org.freshmarker.core.model.primitive;

import java.util.Optional;

public class TemplateString extends TemplatePrimitive<String> {
  public static final TemplateString EMPTY = new TemplateString("");

  public TemplateString(String value) {
    super(value);
  }

  public TemplateString concat(TemplateString other) {
    if (getValue().isEmpty()) {
      return other;
    }
    if (other.getValue().isEmpty()) {
      return this;
    }
    return new TemplateString(getValue() + other.getValue());
  }
  
  @Override
  public Optional<TemplateString> asString() {
    return Optional.of(this);
  }
}
