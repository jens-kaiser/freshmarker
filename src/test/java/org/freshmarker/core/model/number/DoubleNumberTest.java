package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DoubleNumberTest {

    @Test
    void addLongToDouble() {
        DoubleNumber number = new DoubleNumber(10.0);
        assertEquals(20.0, number.add(new LongNumber(10L)).getNumber());
        assertEquals(-10.0, number.add(new LongNumber(-20L)).getNumber());
    }

    @Test
    void addIntegerToDouble() {
        DoubleNumber number = new DoubleNumber(10.0);
        assertEquals(20.0, number.add(new IntegerNumber(10)).getNumber());
        assertEquals(-10.0, number.add(new IntegerNumber(-20)).getNumber());
    }

    @Test
    void addShortToDouble() {
        DoubleNumber number = new DoubleNumber(10.0);
        assertEquals(20.0, number.add(new ShortNumber((short)10)).getNumber());
        assertEquals(-10.0, number.add(new ShortNumber((short)-20)).getNumber());
    }

    @Test
    void addByteToDouble() {
        DoubleNumber number = new DoubleNumber(10.0);
        assertEquals(20.0, number.add(new ByteNumber((byte)10)).getNumber());
        assertEquals(-10.0, number.add(new ByteNumber((byte)-20)).getNumber());
    }

    @Test
    void subLongFromDouble() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(10.0, number.sub(new LongNumber(10L)).getNumber());
        assertEquals(0.0, number.sub(new LongNumber(20L)).getNumber());
    }

    @Test
    void subIntegerFromDouble() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(10.0, number.sub(new IntegerNumber(10)).getNumber());
        assertEquals(0.0, number.sub(new IntegerNumber(20)).getNumber());
    }

    @Test
    void subShortFromDouble() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(10.0, number.sub(new ShortNumber((short)10)).getNumber());
        assertEquals(0.0, number.sub(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void subByteFromDouble() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(10.0, number.sub(new ByteNumber((byte)10)).getNumber());
        assertEquals(0.0, number.sub(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void mulDoubleByLong() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(200.0, number.mul(new LongNumber(10L)).getNumber());
        assertEquals(400.0, number.mul(new LongNumber(20L)).getNumber());
    }

    @Test
    void mulDoubleByInteger() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(200.0, number.mul(new IntegerNumber(10)).getNumber());
        assertEquals(400.0, number.mul(new IntegerNumber(20)).getNumber());
    }

    @Test
    void mulDoubleByShort() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(200.0, number.mul(new ShortNumber((short)10)).getNumber());
        assertEquals(400.0, number.mul(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void mulDoubleByByte() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(200.0, number.mul(new ByteNumber((byte)10)).getNumber());
        assertEquals(400.0, number.mul(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void divDoubleByLong() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(2.0, number.div(new LongNumber(10L)).getNumber());
        assertEquals(1.0, number.div(new LongNumber(20L)).getNumber());
    }

    @Test
    void divDoubleByInteger() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(2.0, number.div(new IntegerNumber(10)).getNumber());
        assertEquals(1.0, number.div(new IntegerNumber(20)).getNumber());
    }

    @Test
    void divDoubleByShort() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(2.0, number.div(new ShortNumber((short)10)).getNumber());
        assertEquals(1.0, number.div(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void divByteFromInteger() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(2.0, number.div(new ByteNumber((byte)10)).getNumber());
        assertEquals(1.0, number.div(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void modDoubleByLong() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(0.0, number.mod(new LongNumber(4L)).getNumber());
        assertEquals(2.0, number.mod(new LongNumber(3L)).getNumber());
    }

    @Test
    void modDoubleByDouble() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(0.0, number.mod(new IntegerNumber(4)).getNumber());
        assertEquals(2.0, number.mod(new IntegerNumber(3)).getNumber());
    }

    @Test
    void modDoubleByShort() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(0.0, number.mod(new ShortNumber((short)4)).getNumber());
        assertEquals(2.0, number.mod(new ShortNumber((short)3)).getNumber());
    }

    @Test
    void modDoubleByByte() {
        DoubleNumber number = new DoubleNumber(20.0);
        assertEquals(0.0, number.mod(new ByteNumber((byte)4)).getNumber());
        assertEquals(2.0, number.mod(new ByteNumber((byte)3)).getNumber());
    }

    @Test
    void abs() {
        assertEquals(42.0, new DoubleNumber(42.0).abs().getNumber());
        assertEquals(42.0, new DoubleNumber(-42.0).abs().getNumber());
    }

    @Test
    void sign() {
        assertEquals(1, new DoubleNumber(42.0).sign().getNumber());
        assertEquals(-1, new DoubleNumber(-42.0).sign().getNumber());
    }

    @Test
    void negate() {
        assertEquals(-42.0, new DoubleNumber(42.0).negate().getNumber());
        assertEquals(42.0, new DoubleNumber(-42.0).negate().getNumber());
    }

    @Test
    void getType() {
        assertEquals(Type.DOUBLE, new DoubleNumber(42.0).getType());
    }

    @Test
    void toType() {
        DoubleNumber doubleNumber = new DoubleNumber(42.0);
        assertInstanceOf(ByteNumber.class, doubleNumber.toType(Type.BYTE));
        assertInstanceOf(ShortNumber.class, doubleNumber.toType(Type.SHORT));
        assertInstanceOf(IntegerNumber.class, doubleNumber.toType(Type.INTEGER));
        assertInstanceOf(LongNumber.class, doubleNumber.toType(Type.LONG));
        assertInstanceOf(FloatNumber.class, doubleNumber.toType(Type.FLOAT));
        assertInstanceOf(DoubleNumber.class, doubleNumber.toType(Type.DOUBLE));
    }
}
