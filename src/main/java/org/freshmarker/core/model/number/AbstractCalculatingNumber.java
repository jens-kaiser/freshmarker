package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;

import java.util.Objects;

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
        return switch (type) {
            case BYTE -> new ByteNumber(byteValue());
            case SHORT -> new ShortNumber(shortValue());
            case INTEGER -> new IntegerNumber(intValue());
            case LONG -> new LongNumber(longValue());
            case FLOAT -> new FloatNumber(floatValue());
            case DOUBLE -> new DoubleNumber(doubleValue());
        };
    }

    public String toString() {
        return wrapped.toString();
    }
}
