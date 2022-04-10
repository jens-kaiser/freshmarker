package org.freshmarker.core.model.primitive;

import java.util.Objects;
import java.util.Optional;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public class TemplatePrimitive<P> implements TemplateObject {

  private final P value;

  public TemplatePrimitive(P value) {
    this.value = Objects.requireNonNull(value);
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public Optional<TemplatePrimitive<?>> asPrimitive() {
    return Optional.of(this);
  }

  public P getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  public boolean equals(Object o) {
    if (o instanceof TemplatePrimitive) {
      return value.equals(((TemplatePrimitive<?>)o).value);
    }
    return false;
  }

  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return this;
  }
}
