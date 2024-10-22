package org.freshmarker.core.model.primitive;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.fragment.RelationType;
import org.freshmarker.core.model.TemplateObject;

import java.math.BigDecimal;
import java.math.BigInteger;
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

            @Override
            public int compare(TemplateNumber first, TemplateNumber second) {
                return Byte.compare(first.getValue().byteValue(), second.getValue().byteValue());
            }
        },
        SHORT {
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

            @Override
            public int compare(TemplateNumber first, TemplateNumber second) {
                return Short.compare(first.getValue().shortValue(), second.getValue().shortValue());
            }
        },
        INTEGER {
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

            @Override
            public int compare(TemplateNumber first, TemplateNumber second) {
                return Integer.compare(first.getValue().intValue(), second.getValue().intValue());
            }
        },
        LONG {
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

            @Override
            public int compare(TemplateNumber first, TemplateNumber second) {
                return Long.compare(first.getValue().longValue(), second.getValue().longValue());
            }
        },
        FLOAT {
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

            @Override
            public int compare(TemplateNumber first, TemplateNumber second) {
                return Float.compare(first.getValue().floatValue(), second.getValue().floatValue());
            }
        },
        DOUBLE {
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

            @Override
            public int compare(TemplateNumber first, TemplateNumber second) {
                return Double.compare(first.getValue().doubleValue(), second.getValue().doubleValue());
            }
        },
        BIG_INTEGER {
            @Override
            public TemplateNumber add(TemplateNumber first, TemplateNumber second) {
                BigInteger firstBigInteger = getBigInteger(first);
                BigInteger secondBigInteger = getBigInteger(second);
                return new TemplateNumber(firstBigInteger.add(secondBigInteger));
            }

            @Override
            public TemplateNumber sub(TemplateNumber first, TemplateNumber second) {
                BigInteger firstBigInteger = getBigInteger(first);
                BigInteger secondBigInteger = getBigInteger(second);
                return new TemplateNumber(firstBigInteger.subtract(secondBigInteger));
            }

            @Override
            public TemplateNumber mul(TemplateNumber first, TemplateNumber second) {
                BigInteger firstBigInteger = getBigInteger(first);
                BigInteger secondBigInteger = getBigInteger(second);
                return new TemplateNumber(firstBigInteger.multiply(secondBigInteger));
            }

            @Override
            public TemplateNumber div(TemplateNumber first, TemplateNumber second) {
                BigInteger firstBigInteger = getBigInteger(first);
                BigInteger secondBigInteger = getBigInteger(second);
                return new TemplateNumber(firstBigInteger.divide(secondBigInteger));
            }

            @Override
            public TemplateNumber mod(TemplateNumber first, TemplateNumber second) {
                BigInteger firstBigInteger = getBigInteger(first);
                BigInteger secondBigInteger = getBigInteger(second);
                return new TemplateNumber(firstBigInteger.mod(secondBigInteger));
            }

            @Override
            public TemplateNumber abs(TemplateNumber number) {
                return new TemplateNumber(getBigInteger(number).abs());
            }

            @Override
            public TemplateNumber sign(TemplateNumber number) {
                return new TemplateNumber(getBigInteger(number).signum());
            }

            @Override
            public TemplateNumber negate(TemplateNumber number) {
                return new TemplateNumber(getBigInteger(number).negate());
            }

            @Override
            public int compare(TemplateNumber first, TemplateNumber second) {
                return getBigInteger(first).compareTo(getBigInteger(second));
            }

            private static BigInteger getBigInteger(TemplateNumber first) {
                return first.getValue() instanceof BigInteger bigInteger ? bigInteger : BigInteger.valueOf(first.getValue().longValue());
            }
        },
        BIG_DECIMAL {
            @Override
            public TemplateNumber add(TemplateNumber first, TemplateNumber second) {
                BigDecimal firstBig = getBigDecimal(first);
                BigDecimal secondBig = getBigDecimal(second);
                return new TemplateNumber(firstBig.add(secondBig));
            }

            @Override
            public TemplateNumber sub(TemplateNumber first, TemplateNumber second) {
                BigDecimal firstBig = getBigDecimal(first);
                BigDecimal secondBig = getBigDecimal(second);
                return new TemplateNumber(firstBig.subtract(secondBig));
            }

            @Override
            public TemplateNumber mul(TemplateNumber first, TemplateNumber second) {
                BigDecimal firstBig = getBigDecimal(first);
                BigDecimal secondBig = getBigDecimal(second);
                return new TemplateNumber(firstBig.multiply(secondBig));
            }

            @Override
            public TemplateNumber div(TemplateNumber first, TemplateNumber second) {
                BigDecimal firstBig = getBigDecimal(first);
                BigDecimal secondBig = getBigDecimal(second);
                return new TemplateNumber(firstBig.divide(secondBig));
            }

            @Override
            public TemplateNumber mod(TemplateNumber first, TemplateNumber second) {
                throw new ProcessException("cannot calculate modulo on BigDecimal");
            }

            @Override
            public TemplateNumber abs(TemplateNumber number) {
                return new TemplateNumber(getBigDecimal(number).abs());
            }

            @Override
            public TemplateNumber sign(TemplateNumber number) {
                return new TemplateNumber(getBigDecimal(number).signum());
            }

            @Override
            public TemplateNumber negate(TemplateNumber number) {
                return new TemplateNumber(getBigDecimal(number).negate());
            }

            @Override
            public int compare(TemplateNumber first, TemplateNumber second) {
                return getBigDecimal(first).compareTo(getBigDecimal(second));
            }

            private static BigDecimal getBigDecimal(TemplateNumber first) {
                return first.getValue() instanceof BigDecimal bigInteger ? bigInteger : BigDecimal.valueOf(first.getValue().longValue());
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

        public TemplateNumber min(TemplateNumber first, TemplateNumber second) {
            return compare(first, second) == -1 ? first : second;
        }

        public TemplateNumber max(TemplateNumber first, TemplateNumber second) {
            return compare(first, second) == 1 ? first : second;
        }

        public abstract int compare(TemplateNumber first, TemplateNumber second);

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

    public TemplateNumber(BigInteger value) {
        this(value, Type.BIG_INTEGER);
    }

    public TemplateNumber(BigDecimal value) {
        this(value, Type.BIG_DECIMAL);
    }

    public TemplateNumber(byte value) {
        this(value, Type.BYTE);
    }

    public TemplateNumber(short value) {
        this(value, Type.SHORT);
    }

    public TemplateNumber(int value) {
        this(value, Type.INTEGER);
    }

    public TemplateNumber(long value) {
        this(value, Type.LONG);
    }

    public TemplateNumber(double value) {
        this(value, Type.DOUBLE);
    }

    public TemplateNumber(float value) {
        this(value, Type.FLOAT);
    }

    public TemplateNumber(Number number, Type type) {
        super(number);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public TemplateNumber add(TemplateObject other, ProcessContext context) {
        TemplateNumber number = other.evaluate(context, TemplateNumber.class);
        return Type.getNewType(this, number).add(this, number);
    }

    @Override
    public TemplateNumber subtract(TemplateObject other, ProcessContext context) {
        TemplateNumber number = other.evaluate(context, TemplateNumber.class);
        return Type.getNewType(this, number).sub(this, number);
    }

    @Override
    public TemplateNumber multiply(TemplateObject other, ProcessContext context) {
        TemplateNumber number = other.evaluate(context, TemplateNumber.class);
        return Type.getNewType(this, number).mul(this, number);
    }

    @Override
    public TemplateNumber divide(TemplateObject other, ProcessContext context) {
        TemplateNumber number = other.evaluate(context, TemplateNumber.class);
        return Type.getNewType(this, number).div(this, number);
    }

    @Override
    public TemplateNumber modulo(TemplateObject other, ProcessContext context) {
        TemplateNumber number = other.evaluate(context, TemplateNumber.class);
        return Type.getNewType(this, number).mod(this, number);
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

    public TemplateNumber min(TemplateNumber other) {
        return Type.getNewType(this, other).min(this, other);
    }

    public TemplateNumber max(TemplateNumber other) {
        return Type.getNewType(this, other).max(this, other);
    }

    public int asInt() {
        return getValue().intValue();
    }

    @Override
    public boolean relation(RelationType operator, TemplateObject operand, ProcessContext context) {
        TemplateNumber evaluate = operand.evaluate(context, TemplateNumber.class);
        return operator.compare(Type.getNewType(this, evaluate).compare(this, evaluate));
    }
}
