package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public class ShortNumber extends AbstractCalculatingNumber<Short>{

  public ShortNumber(Short wrapped) {
    super(wrapped);
  }

  @Override
  public CalculatingNumber add(CalculatingNumber value) {
    return new ShortNumber((short)(wrapped + value.getNumber().shortValue()));
  }

  @Override
  public CalculatingNumber sub(CalculatingNumber value) {
    return new ShortNumber((short)(wrapped - value.getNumber().shortValue()));
  }

  @Override
  public CalculatingNumber mul(CalculatingNumber value) {
    return new ShortNumber((short)(wrapped * value.getNumber().shortValue()));
  }

  @Override
  public CalculatingNumber div(CalculatingNumber value) {
    return new ShortNumber((short)(wrapped / value.getNumber().shortValue()));
  }

  @Override
  public CalculatingNumber mod(CalculatingNumber value) {
    return new ShortNumber((short)(wrapped % value.getNumber().shortValue()));
  }

  @Override
  public CalculatingNumber abs() {
    return new ShortNumber((short)(wrapped >= 0 ? wrapped : -(short)wrapped));
  }

  @Override
  public CalculatingNumber sign() {
    return new IntegerNumber(Short.compare(wrapped, (short) 0));
  }

  @Override
  public CalculatingNumber negate() {
    return new ShortNumber((short)(-wrapped));
  }

  @Override
  public Type getType() {
    return Type.SHORT;
  }
}
