package org.freshmarker.core.model.primitive;

import java.util.Optional;

import org.freshmarker.core.model.number.ByteNumber;
import org.freshmarker.core.model.number.CalculatingNumber;
import org.freshmarker.core.model.number.DoubleNumber;
import org.freshmarker.core.model.number.FloatNumber;
import org.freshmarker.core.model.number.IntegerNumber;
import org.freshmarker.core.model.number.LongNumber;
import org.freshmarker.core.model.number.ShortNumber;

public class TemplateNumber extends TemplatePrimitive<CalculatingNumber> {

  public enum Type {
    BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE
  }

  public TemplateNumber(CalculatingNumber value) {
    super(value);
  }

  public TemplateNumber(byte value) {
    super(new ByteNumber(value));
  }

  public TemplateNumber(short value) {
    super(new ShortNumber(value));
  }

  public TemplateNumber(int value) {
    super(new IntegerNumber(value));
  }

  public TemplateNumber(long value) {
    super(new LongNumber(value));
  }

  public TemplateNumber(double value) {
    super(new DoubleNumber(value));
  }

  public TemplateNumber(float value) {
    super(new FloatNumber(value));
  }

  public TemplateNumber add(TemplateNumber other) {
    return new TemplateNumber(getValue().toType(getNewType(other)).add(other.getValue()));
  }

  public TemplateNumber subtract(TemplateNumber other) {
    return new TemplateNumber(getValue().toType(getNewType(other)).sub(other.getValue()));
  }

  public TemplateNumber multiply(TemplateNumber other) {
    return new TemplateNumber(getValue().toType(getNewType(other)).mul(other.getValue()));
  }

  public TemplateNumber divide(TemplateNumber other) {
    return new TemplateNumber(getValue().toType(getNewType(other)).div(other.getValue()));
  }

  public TemplateNumber modulo(TemplateNumber other) {
    return new TemplateNumber(getValue().toType(getNewType(other)).mod(other.getValue()));
  }

  private Type getNewType(TemplateNumber other) {
    return Type.values()[Math.max(getValue().getType().ordinal(), other.getValue().getType().ordinal())];
  }

  public TemplateNumber sign() {
    return new TemplateNumber(getValue().sign());
  }

  public TemplateNumber abs() {
    return new TemplateNumber(getValue().abs());
  }

  public TemplateNumber negate() {
    return new TemplateNumber(getValue().negate());
  }

  public TemplateNumber compare(TemplateNumber other) {
    return subtract(other);
  }

  @Override
  public Optional<TemplateNumber> asNumber() {
    return Optional.of(this);
  }

  public int asInt() {
    return getValue().getNumber().intValue();
  }

  @Override
  public boolean isNumber() {
    return true;
  }
}
