package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public class FloatNumber extends AbstractCalculatingNumber<Float> {

  public FloatNumber(Float wrapped) {
    super(wrapped);
  }

  @Override
  public CalculatingNumber add(CalculatingNumber value) {
    return new FloatNumber(wrapped + value.getNumber().floatValue());
  }

  @Override
  public CalculatingNumber sub(CalculatingNumber value) {
    return new FloatNumber(wrapped - value.getNumber().floatValue());
  }

  @Override
  public CalculatingNumber mul(CalculatingNumber value) {
    return new FloatNumber(wrapped * value.getNumber().floatValue());
  }

  @Override
  public CalculatingNumber div(CalculatingNumber value) {
    return new FloatNumber(wrapped / value.getNumber().floatValue());
  }

  @Override
  public CalculatingNumber mod(CalculatingNumber value) {
    return new FloatNumber(wrapped % value.getNumber().floatValue());
  }

  @Override
  public CalculatingNumber abs() {
    return new FloatNumber(wrapped >= 0 ? wrapped : -(float) wrapped);
  }

  @Override
  public CalculatingNumber sign() {
    return new IntegerNumber((int)Math.signum(wrapped));
  }

  @Override
  public CalculatingNumber negate() {
    return new FloatNumber(-wrapped);
  }

  @Override
  public Type getType() {
    return Type.FLOAT;
  }
}
