package org.freshmarker.core.model.number;

import java.util.Objects;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public abstract class AbstractCalculatingNumber<N extends Number> extends Number implements CalculatingNumber {

  protected final N wrapped;

  protected AbstractCalculatingNumber(N wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public int intValue() {
    return wrapped.intValue();
  }

  @Override
  public long longValue() {
    return wrapped.longValue();
  }

  @Override
  public float floatValue() {
    return wrapped.floatValue();
  }

  @Override
  public double doubleValue() {
    return wrapped.floatValue();
  }

  @Override
  public N getNumber() {
    return wrapped;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractCalculatingNumber<?> that = (AbstractCalculatingNumber<?>) o;
    return Objects.equals(wrapped, that.wrapped);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wrapped);
  }

  @Override
  public CalculatingNumber toType(Type type) {
    if (getType() == type) {
      return this;
    }
    switch (type) {
      case BYTE:
        return new ByteNumber(wrapped.byteValue());
      case SHORT:
        return new ShortNumber(wrapped.shortValue());
      case INTEGER:
        return new IntegerNumber(wrapped.intValue());
      case LONG:
        return new LongNumber(wrapped.longValue());
      case FLOAT:
        return new FloatNumber(wrapped.floatValue());
      case DOUBLE:
        return new DoubleNumber(wrapped.doubleValue());
      default:throw new ProcessException("unknwon type: " + type);
    }
  }
}
