package org.freshmarker.core.model.primitive;

import java.util.Optional;
import java.util.stream.IntStream;

public class TemplateNumber extends TemplatePrimitive<Number> {

    public enum Type {
        BYTE {
            @Override
            public TemplateNumber add(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((byte) (first.getValue().byteValue() + second.getValue().byteValue()));
            }

            @Override
            public TemplateNumber sub(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((byte) (first.getValue().byteValue() - second.getValue().byteValue()));
            }

            @Override
            public TemplateNumber mul(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((byte) (first.getValue().byteValue() * second.getValue().byteValue()));
            }

            @Override
            public TemplateNumber div(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((byte) (first.getValue().byteValue() / second.getValue().byteValue()));
            }

            @Override
            public TemplateNumber mod(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((byte) (first.getValue().byteValue() % second.getValue().byteValue()));
            }

            @Override
            public TemplateNumber abs(TemplateNumber number) {
                return number.getValue().byteValue() > 0 ? number : new TemplateNumber((byte) -number.getValue().byteValue());
            }

            @Override
            public TemplateNumber sign(TemplateNumber number) {
                return new TemplateNumber(Integer.signum(number.getValue().intValue()));
            }

            @Override
            public TemplateNumber negate(TemplateNumber number) {
                return new TemplateNumber((byte) -number.getValue().byteValue());
            }
        }, SHORT {
            @Override
            public TemplateNumber add(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((short) (first.getValue().shortValue() + second.getValue().shortValue()));
            }

            @Override
            public TemplateNumber sub(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((short) (first.getValue().shortValue() - second.getValue().shortValue()));
            }

            @Override
            public TemplateNumber mul(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((short) (first.getValue().shortValue() * second.getValue().shortValue()));
            }

            @Override
            public TemplateNumber div(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((short) (first.getValue().shortValue() / second.getValue().shortValue()));
            }

            @Override
            public TemplateNumber mod(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber((short) (first.getValue().shortValue() % second.getValue().shortValue()));
            }

            @Override
            public TemplateNumber abs(TemplateNumber number) {
                return number.getValue().shortValue() > 0 ? number : new TemplateNumber((short) -number.getValue().shortValue());
            }

            @Override
            public TemplateNumber sign(TemplateNumber number) {
                return new TemplateNumber(Integer.signum(number.getValue().intValue()));
            }

            @Override
            public TemplateNumber negate(TemplateNumber number) {
                return new TemplateNumber((short) -number.getValue().shortValue());
            }
        }, INTEGER {
            @Override
            public TemplateNumber add(TemplateNumber first, TemplateNumber second) {
                int firstValue = first.getValue().intValue();
                int secondValue = second.getValue().intValue();
                if (firstValue == 0) {
                    return second;
                }
                if (secondValue == 0) {
                    return first;
                }
                return TemplateNumber.of(firstValue + secondValue);
            }

            @Override
            public TemplateNumber sub(TemplateNumber first, TemplateNumber second) {
                int firstValue = first.getValue().intValue();
                int secondValue = second.getValue().intValue();
                if (firstValue == 0) {
                    return second;
                }
                if (secondValue == 0) {
                    return first;
                }
                return TemplateNumber.of(firstValue - secondValue);
            }

            @Override
            public TemplateNumber mul(TemplateNumber first, TemplateNumber second) {
                int firstValue = first.getValue().intValue();
                int secondValue = second.getValue().intValue();
                if (firstValue == 1) {
                    return second;
                }
                if (secondValue == 1) {
                    return first;
                }
                return TemplateNumber.of(firstValue * secondValue);
            }

            @Override
            public TemplateNumber div(TemplateNumber first, TemplateNumber second) {
                return TemplateNumber.of(first.getValue().intValue() / second.getValue().intValue());
            }

            @Override
            public TemplateNumber mod(TemplateNumber first, TemplateNumber second) {
                return TemplateNumber.of(first.getValue().intValue() % second.getValue().intValue());
            }

            @Override
            public TemplateNumber abs(TemplateNumber number) {
                int value = number.getValue().intValue();
                return value > 0 ? number : TemplateNumber.of(-value);
            }

            @Override
            public TemplateNumber sign(TemplateNumber number) {
                return TemplateNumber.of(Integer.signum(number.getValue().intValue()));
            }

            @Override
            public TemplateNumber negate(TemplateNumber number) {
                return TemplateNumber.of(-number.getValue().intValue());
            }
        }, LONG {
            @Override
            public TemplateNumber add(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().longValue() + second.getValue().longValue());
            }

            @Override
            public TemplateNumber sub(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().longValue() - second.getValue().longValue());
            }

            @Override
            public TemplateNumber mul(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().longValue() * second.getValue().longValue());
            }

            @Override
            public TemplateNumber div(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().longValue() / second.getValue().longValue());
            }

            @Override
            public TemplateNumber mod(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().longValue() % second.getValue().longValue());
            }

            @Override
            public TemplateNumber abs(TemplateNumber number) {
                return number.getValue().longValue() > 0 ? number : new TemplateNumber(-number.getValue().longValue());
            }

            @Override
            public TemplateNumber sign(TemplateNumber number) {
                return new TemplateNumber(Long.signum(number.getValue().longValue()));
            }

            @Override
            public TemplateNumber negate(TemplateNumber number) {
                return new TemplateNumber(-number.getValue().longValue());
            }
        }, FLOAT {
            @Override
            public TemplateNumber add(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().floatValue() + second.getValue().floatValue());
            }

            @Override
            public TemplateNumber sub(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().floatValue() - second.getValue().floatValue());
            }

            @Override
            public TemplateNumber mul(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().floatValue() * second.getValue().floatValue());
            }

            @Override
            public TemplateNumber div(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().floatValue() / second.getValue().floatValue());
            }

            @Override
            public TemplateNumber mod(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().floatValue() % second.getValue().floatValue());
            }

            @Override
            public TemplateNumber abs(TemplateNumber number) {
                return number.getValue().floatValue() > 0 ? number : new TemplateNumber(-number.getValue().floatValue());
            }

            @Override
            public TemplateNumber sign(TemplateNumber number) {
                return new TemplateNumber(Math.signum(number.getValue().floatValue()));
            }

            @Override
            public TemplateNumber negate(TemplateNumber number) {
                return new TemplateNumber(-number.getValue().floatValue());
            }
        }, DOUBLE {
            @Override
            public TemplateNumber add(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().doubleValue() + second.getValue().doubleValue());
            }

            @Override
            public TemplateNumber sub(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().doubleValue() - second.getValue().doubleValue());
            }

            @Override
            public TemplateNumber mul(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().doubleValue() * second.getValue().doubleValue());
            }

            @Override
            public TemplateNumber div(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().doubleValue() / second.getValue().doubleValue());
            }

            @Override
            public TemplateNumber mod(TemplateNumber first, TemplateNumber second) {
                return new TemplateNumber(first.getValue().doubleValue() % second.getValue().doubleValue());
            }

            @Override
            public TemplateNumber abs(TemplateNumber number) {
                return number.getValue().doubleValue() > 0 ? number : new TemplateNumber(-number.getValue().doubleValue());
            }

            @Override
            public TemplateNumber sign(TemplateNumber number) {
                return new TemplateNumber(Math.signum(number.getValue().doubleValue()));
            }

            @Override
            public TemplateNumber negate(TemplateNumber number) {
                return new TemplateNumber(-number.getValue().doubleValue());
            }
        };

        public boolean isFloatingPoint() {
            return this == FLOAT || this == DOUBLE;
        }

        public abstract TemplateNumber add(TemplateNumber first, TemplateNumber second);

        public abstract TemplateNumber sub(TemplateNumber first, TemplateNumber second);

        public abstract TemplateNumber mul(TemplateNumber first, TemplateNumber second);

        public abstract TemplateNumber div(TemplateNumber first, TemplateNumber second);

        public abstract TemplateNumber mod(TemplateNumber first, TemplateNumber second);

        public abstract TemplateNumber abs(TemplateNumber number);

        public abstract TemplateNumber sign(TemplateNumber number);

        public abstract TemplateNumber negate(TemplateNumber number);

        public static Type getNewType(TemplateNumber first, TemplateNumber second) {
            return Type.values()[Math.max(first.getType().ordinal(), second.getType().ordinal())];
        }
    }

    private static final TemplateNumber[] INTEGERS = IntStream.range(-32, 33).mapToObj(TemplateNumber::new).toArray(TemplateNumber[]::new);

    public static TemplateNumber of(int i) {
        if (i < -32 || i > 32) {
            return new TemplateNumber(i);
        }
        return INTEGERS[i + 32];
    }

    public static TemplateNumber of(Number number, Type type) {
        if (type == Type.INTEGER) {
            return of((Integer) number);
        }
        return new TemplateNumber(number, type);
    }

    private final Type type;

    public TemplateNumber(byte value) {
        super(value);
        type = Type.BYTE;
    }

    public TemplateNumber(short value) {
        super(value);
        type = Type.SHORT;
    }

    public TemplateNumber(int value) {
        super(value);
        type = Type.INTEGER;
    }

    public TemplateNumber(long value) {
        super(value);
        type = Type.LONG;
    }

    public TemplateNumber(double value) {
        super(value);
        type = Type.DOUBLE;
    }

    public TemplateNumber(float value) {
        super(value);
        type = Type.FLOAT;
    }

    public TemplateNumber(Number number, Type type) {
        super(number);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public TemplateNumber add(TemplateNumber other) {
        return Type.getNewType(this, other).add(this, other);
    }

    public TemplateNumber subtract(TemplateNumber other) {
        return Type.getNewType(this, other).sub(this, other);
    }

    public TemplateNumber multiply(TemplateNumber other) {
        return Type.getNewType(this, other).mul(this, other);
    }

    public TemplateNumber divide(TemplateNumber other) {
        return Type.getNewType(this, other).div(this, other);
    }

    public TemplateNumber modulo(TemplateNumber other) {
        return Type.getNewType(this, other).mod(this, other);
    }

    public TemplateNumber sign() {
        return getType().sign(this);
    }

    public TemplateNumber abs() {
        return getType().abs(this);
    }

    public TemplateNumber negate() {
        return getType().negate(this);
    }

    public TemplateNumber compare(TemplateNumber other) {
        return subtract(other);
    }

    @Override
    public Optional<TemplateNumber> asNumber() {
        return Optional.of(this);
    }

    public int asInt() {
        return getValue().intValue();
    }
}
