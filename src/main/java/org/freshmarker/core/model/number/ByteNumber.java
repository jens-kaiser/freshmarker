package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public class ByteNumber extends AbstractCalculatingNumber<Byte> {

  public ByteNumber(Byte wrapped) {
    super(wrapped);
  }

  @Override
  public CalculatingNumber add(CalculatingNumber value) {
    return new ByteNumber((byte) (wrapped + value.getNumber().byteValue()));
  }

  @Override
  public CalculatingNumber sub(CalculatingNumber value) {
    return new ByteNumber((byte) (wrapped - value.getNumber().byteValue()));
  }

  @Override
  public CalculatingNumber mul(CalculatingNumber value) {
    return new ByteNumber((byte) (wrapped * value.getNumber().byteValue()));
  }

  @Override
  public CalculatingNumber div(CalculatingNumber value) {
    return new ByteNumber((byte) (wrapped / value.getNumber().byteValue()));
  }

  @Override
  public CalculatingNumber mod(CalculatingNumber value) {
    return new ByteNumber((byte) (wrapped % value.getNumber().byteValue()));
  }

  @Override
  public CalculatingNumber abs() {
    return new ByteNumber((byte) (wrapped >= 0 ? wrapped : -(byte) wrapped));
  }

  @Override
  public CalculatingNumber sign() {
    return new IntegerNumber(Byte.compare(wrapped, (byte) 0));
  }

  @Override
  public CalculatingNumber negate() {
    return new ByteNumber((byte) (-wrapped));
  }

  @Override
  public Type getType() {
    return Type.BYTE;
  }
}
