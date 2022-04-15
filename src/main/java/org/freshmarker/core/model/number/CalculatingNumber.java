package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public interface CalculatingNumber {
CalculatingNumber add(CalculatingNumber value);
  CalculatingNumber sub(CalculatingNumber value);
  CalculatingNumber mul(CalculatingNumber value);
  CalculatingNumber div(CalculatingNumber value);
  CalculatingNumber mod(CalculatingNumber value);
  CalculatingNumber abs();
  CalculatingNumber sign();
  CalculatingNumber negate();
  CalculatingNumber toType(Type type);
  Number getNumber();
  Type getType();
}
