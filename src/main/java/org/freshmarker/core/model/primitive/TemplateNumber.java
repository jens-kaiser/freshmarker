package org.freshmarker.core.model.primitive;

import java.util.Optional;
import org.freshmarker.core.ProcessException;

public class TemplateNumber extends TemplatePrimitive<Number> {

  public enum Type {
    BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE;
  }

  private final Type type;

  public TemplateNumber(Number value, Type type) {
    super(value);
    this.type = type;
  }

  public TemplateNumber add(TemplateNumber other) {
    Type newType = Type.values()[Math.max(type.ordinal(), other.type.ordinal())];
    switch (newType) {
      case BYTE:
        return new TemplateNumber(getValue().byteValue() + other.getValue().byteValue(), newType);
      case SHORT:
        return new TemplateNumber(getValue().shortValue() + other.getValue().shortValue(), newType);
      case INTEGER:
        return new TemplateNumber(getValue().intValue() + other.getValue().intValue(),newType);
      case LONG:
        return new TemplateNumber(getValue().longValue() + other.getValue().longValue(), newType);
      case FLOAT:
        return new TemplateNumber(getValue().floatValue() + other.getValue().floatValue(), newType);
      case DOUBLE:
        return new TemplateNumber(getValue().doubleValue() + other.getValue().doubleValue(), newType);
    }
    throw new ProcessException("something went wrong");
  }

  public TemplateNumber subtract(TemplateNumber other) {
    Type newType = Type.values()[Math.max(type.ordinal(), other.type.ordinal())];
    switch (newType) {
      case BYTE:
        return new TemplateNumber(getValue().byteValue() - other.getValue().byteValue(), newType);
      case SHORT:
        return new TemplateNumber(getValue().shortValue() - other.getValue().shortValue(), newType);
      case INTEGER:
        return new TemplateNumber(getValue().intValue() - other.getValue().intValue(),newType);
      case LONG:
        return new TemplateNumber(getValue().longValue() - other.getValue().longValue(), newType);
      case FLOAT:
        return new TemplateNumber(getValue().floatValue() - other.getValue().floatValue(), newType);
      case DOUBLE:
        return new TemplateNumber(getValue().doubleValue() - other.getValue().doubleValue(), newType);
    }
    throw new ProcessException("something went wrong");
  }

  public TemplateNumber multiply(TemplateNumber other) {
    Type newType = Type.values()[Math.max(type.ordinal(), other.type.ordinal())];
    switch (newType) {
      case BYTE:
        return new TemplateNumber(getValue().byteValue() * other.getValue().byteValue(), newType);
      case SHORT:
        return new TemplateNumber(getValue().shortValue() * other.getValue().shortValue(), newType);
      case INTEGER:
        return new TemplateNumber(getValue().intValue() * other.getValue().intValue(),newType);
      case LONG:
        return new TemplateNumber(getValue().longValue() * other.getValue().longValue(), newType);
      case FLOAT:
        return new TemplateNumber(getValue().floatValue() * other.getValue().floatValue(), newType);
      case DOUBLE:
        return new TemplateNumber(getValue().doubleValue() * other.getValue().doubleValue(), newType);
    }
    throw new ProcessException("something went wrong");
  }

  public TemplateNumber divide(TemplateNumber other) {
    Type newType = Type.values()[Math.max(type.ordinal(), other.type.ordinal())];
    switch (newType) {
      case BYTE:
        return new TemplateNumber(getValue().byteValue() / other.getValue().byteValue(), newType);
      case SHORT:
        return new TemplateNumber(getValue().shortValue() / other.getValue().shortValue(), newType);
      case INTEGER:
        return new TemplateNumber(getValue().intValue() / other.getValue().intValue(),newType);
      case LONG:
        return new TemplateNumber(getValue().longValue() / other.getValue().longValue(), newType);
      case FLOAT:
        return new TemplateNumber(getValue().floatValue() / other.getValue().floatValue(), newType);
      case DOUBLE:
        return new TemplateNumber(getValue().doubleValue() / other.getValue().doubleValue(), newType);
    }
    throw new ProcessException("something went wrong");
  }

  public TemplateNumber modulo(TemplateNumber other) {
    Type newType = Type.values()[Math.max(type.ordinal(), other.type.ordinal())];
    switch (newType) {
      case BYTE:
        return new TemplateNumber(getValue().byteValue() % other.getValue().byteValue(), newType);
      case SHORT:
        return new TemplateNumber(getValue().shortValue() % other.getValue().shortValue(), newType);
      case INTEGER:
        return new TemplateNumber(getValue().intValue() % other.getValue().intValue(),newType);
      case LONG:
        return new TemplateNumber(getValue().longValue() % other.getValue().longValue(), newType);
      case FLOAT:
        return new TemplateNumber(getValue().floatValue() % other.getValue().floatValue(), newType);
      case DOUBLE:
        return new TemplateNumber(getValue().doubleValue() % other.getValue().doubleValue(), newType);
    }
    throw new ProcessException("something went wrong");
  }


  public TemplateNumber compare(TemplateNumber other) {
    return other.subtract(this);
  }

  public Optional<TemplateNumber> asNumber() {
    return Optional.of(this);
  }

  public boolean isNumber() {
    return true;
  }
}
