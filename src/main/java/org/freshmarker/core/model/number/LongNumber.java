package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public class LongNumber extends AbstractCalculatingNumber<Long> {

  public LongNumber(Long wrapped) {
    super(wrapped);
  }

  @Override
  public CalculatingNumber add(CalculatingNumber value) {
    return new LongNumber(wrapped + value.getNumber().longValue());
  }

  @Override
  public CalculatingNumber sub(CalculatingNumber value) {
    return new LongNumber(wrapped - value.getNumber().longValue());
  }

  @Override
  public CalculatingNumber mul(CalculatingNumber value) {
    return new LongNumber(wrapped * value.getNumber().longValue());
  }

  @Override
  public CalculatingNumber div(CalculatingNumber value) {
    return new LongNumber(wrapped / value.getNumber().longValue());
  }

  @Override
  public CalculatingNumber mod(CalculatingNumber value) {
    return new LongNumber(wrapped % value.getNumber().longValue());
  }

  @Override
  public CalculatingNumber abs() {
    return new LongNumber(wrapped >= 0 ? wrapped : - wrapped);
  }

  @Override
  public CalculatingNumber sign() {
    return new IntegerNumber(Long.signum(wrapped));
  }

  @Override
  public CalculatingNumber negate() {
    return new LongNumber(-wrapped);
  }

  @Override
  public Type getType() {
    return Type.LONG;
  }
}
