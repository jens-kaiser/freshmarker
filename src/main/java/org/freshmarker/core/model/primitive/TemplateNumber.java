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

  public TemplateNumber(Number value) {
    super(value);
    this.type = Type.INTEGER;
  }

  public TemplateNumber add(TemplateNumber other) {
    Type newType = Type.values()[Math.max(type.ordinal(), other.type.ordinal())];
    switch (newType) {
      case BYTE:
        return new TemplateNumber(getValue().byteValue() + other.getValue().byteValue(), newType);
      case SHORT:
        return new TemplateNumber(getValue().shortValue() + other.getValue().shortValue(), newType);
      case INTEGER:
        return new TemplateNumber(getValue().intValue() + other.getValue().intValue(), newType);
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
        return new TemplateNumber(getValue().intValue() - other.getValue().intValue(), newType);
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
        return new TemplateNumber(getValue().intValue() * other.getValue().intValue(), newType);
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
        return new TemplateNumber(getValue().intValue() / other.getValue().intValue(), newType);
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
        return new TemplateNumber(getValue().intValue() % other.getValue().intValue(), newType);
      case LONG:
        return new TemplateNumber(getValue().longValue() % other.getValue().longValue(), newType);
      case FLOAT:
        return new TemplateNumber(getValue().floatValue() % other.getValue().floatValue(), newType);
      case DOUBLE:
        return new TemplateNumber(getValue().doubleValue() % other.getValue().doubleValue(), newType);
    }
    throw new ProcessException("something went wrong");
  }

  public TemplateNumber sign() {
    switch (type) {
      case BYTE:
        return new TemplateNumber(Byte.compare(getValue().byteValue(), (byte) 0));
      case SHORT:
        return new TemplateNumber(Short.compare(getValue().shortValue(), (short) 0));
      case INTEGER:
        return new TemplateNumber(Integer.compare(getValue().intValue(), 0));
      case LONG:
        return new TemplateNumber(Long.compare(getValue().longValue(), 0L));
      case FLOAT:
        return new TemplateNumber(Float.compare(getValue().floatValue(), 0F));
      case DOUBLE:
        return new TemplateNumber(Double.compare(getValue().doubleValue(), 0D));
    }
    throw new ProcessException("something went wrong");
  }

  public TemplateNumber abs() {
    switch (type) {
      case BYTE:
        byte byteValue = getValue().byteValue();
        return new TemplateNumber(byteValue >= 0 ? byteValue : -byteValue, Type.BYTE);
      case SHORT:
        short shortValue = getValue().shortValue();
        return new TemplateNumber(shortValue >= 0 ? shortValue : -shortValue, Type.SHORT);
      case INTEGER:
        int intValue = getValue().intValue();
        System.out.println(intValue);
        return new TemplateNumber(intValue >= 0 ? intValue : -intValue);
      case LONG:
        long longValue = getValue().longValue();
        return new TemplateNumber(longValue >= 0 ? longValue : -longValue, Type.LONG);
      case FLOAT:
        float floatValue = getValue().floatValue();
        return new TemplateNumber(floatValue >= 0 ? floatValue : -floatValue, Type.FLOAT);
      case DOUBLE:
        double doubleValue = getValue().doubleValue();
        return new TemplateNumber(doubleValue >= 0 ? doubleValue : -doubleValue, Type.DOUBLE);
    }
    throw new ProcessException("something went wrong");
  }

  public TemplateNumber negate() {
    switch (type) {
      case BYTE:
        return new TemplateNumber(-getValue().byteValue());
      case SHORT:
        return new TemplateNumber(-getValue().shortValue());
      case INTEGER:
        return new TemplateNumber(-getValue().intValue());
      case LONG:
        return new TemplateNumber(-getValue().longValue());
      case FLOAT:
        return new TemplateNumber(-getValue().floatValue());
      case DOUBLE:
        return new TemplateNumber(-getValue().doubleValue());
    }
    throw new ProcessException("something went wrong");
  }

  public TemplateNumber compare(TemplateNumber other) {
    return subtract(other);
  }

  @Override
  public Optional<TemplateNumber> asNumber() {
    return Optional.of(this);
  }

  public int asInt() {
    return getValue().intValue();
  }

  @Override
  public boolean isNumber() {
    return true;
  }
}
