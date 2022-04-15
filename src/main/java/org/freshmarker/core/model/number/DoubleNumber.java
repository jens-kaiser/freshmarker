package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public class DoubleNumber extends AbstractCalculatingNumber<Double>{

  public DoubleNumber(Double wrapped) {
    super(wrapped);
  }

  @Override
  public CalculatingNumber add(CalculatingNumber value) {
    return new DoubleNumber(wrapped + value.getNumber().doubleValue());
  }

  @Override
  public CalculatingNumber sub(CalculatingNumber value) {
    return new DoubleNumber(wrapped - value.getNumber().doubleValue());
  }

  @Override
  public CalculatingNumber mul(CalculatingNumber value) {
    return new DoubleNumber(wrapped * value.getNumber().doubleValue());
  }

  @Override
  public CalculatingNumber div(CalculatingNumber value) {
    return new DoubleNumber(wrapped / value.getNumber().doubleValue());
  }

  @Override
  public CalculatingNumber mod(CalculatingNumber value) {
    return new DoubleNumber(wrapped % value.getNumber().doubleValue());
  }

  @Override
  public CalculatingNumber abs() {
    return new DoubleNumber(wrapped >= 0 ? wrapped : -(double)wrapped);
  }

  @Override
  public CalculatingNumber sign() {
    return new IntegerNumber(Double.compare(wrapped, 0.0));
  }

  @Override
  public CalculatingNumber negate() {
    return new DoubleNumber(-wrapped);
  }

  @Override
  public Type getType() {
    return Type.DOUBLE;
  }
}
