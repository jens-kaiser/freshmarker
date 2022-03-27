package org.freshmarker.core.model.primitive;

import java.util.Objects;
import java.util.Optional;
import org.freshmarker.core.Environment;

public class TemplatePrimitive<P> implements TemplateObject {

  private final P value;

  public TemplatePrimitive(P value) {
    this.value = Objects.requireNonNull(value);
  }

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

  @Override
  public String evaluate(Environment environment) {
    return environment.getFormatter(getClass()).format(this, environment.getLocale());
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
}
