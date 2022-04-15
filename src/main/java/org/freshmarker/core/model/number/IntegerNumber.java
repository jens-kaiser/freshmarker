package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public class IntegerNumber extends AbstractCalculatingNumber<Integer>{

  public IntegerNumber(Integer wrapped) {
    super(wrapped);
  }

  @Override
  public CalculatingNumber add(CalculatingNumber value) {
    return new IntegerNumber(wrapped + value.getNumber().intValue());
  }

  @Override
  public CalculatingNumber sub(CalculatingNumber value) {
    return new IntegerNumber(wrapped - value.getNumber().intValue());
  }

  @Override
  public CalculatingNumber mul(CalculatingNumber value) {
    return new IntegerNumber(wrapped * value.getNumber().intValue());
  }

  @Override
  public CalculatingNumber div(CalculatingNumber value) {
    return new IntegerNumber(wrapped / value.getNumber().intValue());
  }

  @Override
  public CalculatingNumber mod(CalculatingNumber value) {
    return new IntegerNumber(wrapped % value.getNumber().intValue());
  }

  @Override
  public CalculatingNumber abs() {
    return new IntegerNumber(wrapped >= 0 ? wrapped : -(int)wrapped);
  }

  @Override
  public CalculatingNumber sign() {
    return new IntegerNumber(Integer.compare(wrapped, 0));
  }

  @Override
  public CalculatingNumber negate() {
    return new IntegerNumber(-wrapped);
  }

  @Override
  public Type getType() {
    return Type.INTEGER;
  }
}
