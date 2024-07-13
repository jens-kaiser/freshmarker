package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ByteNumberTest {

    @Test
    void addLongToByte() {
        ByteNumber number1 = new ByteNumber((byte)10);
        assertEquals((byte)20, number1.add(new LongNumber(10L)).getNumber());
        assertEquals((byte)-10, number1.add(new LongNumber(-20L)).getNumber());
    }

    @Test
    void addIntegerToByte() {
        ByteNumber number1 = new ByteNumber((byte)10);
        assertEquals((byte)20, number1.add(new IntegerNumber(10)).getNumber());
        assertEquals((byte)-10, number1.add(new IntegerNumber(-20)).getNumber());
    }

    @Test
    void addShortToByte() {
        ByteNumber number1 = new ByteNumber((byte)10);
        assertEquals((byte)20, number1.add(new ShortNumber((short)10)).getNumber());
        assertEquals((byte)-10, number1.add(new ShortNumber((short)-20)).getNumber());
    }

    @Test
    void addByteToByte() {
        ByteNumber number1 = new ByteNumber((byte)10);
        assertEquals((byte)20, number1.add(new ByteNumber((byte)10)).getNumber());
        assertEquals((byte)-10, number1.add(new ByteNumber((byte)-20)).getNumber());
    }

    @Test
    void subLongFromLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)10, number1.sub(new LongNumber(10L)).getNumber());
        assertEquals((byte)0, number1.sub(new LongNumber(20L)).getNumber());
    }

    @Test
    void subIntegerFromLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)10, number1.sub(new IntegerNumber(10)).getNumber());
        assertEquals((byte)0, number1.sub(new IntegerNumber(20)).getNumber());
    }

    @Test
    void subShortFromLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)10, number1.sub(new ShortNumber((short)10)).getNumber());
        assertEquals((byte)0, number1.sub(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void subByteFromLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)10, number1.sub(new ByteNumber((byte)10)).getNumber());
        assertEquals((byte)0, number1.sub(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void mulLongByByte() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)200, number1.mul(new LongNumber(10L)).getNumber());
        assertEquals((byte)400, number1.mul(new LongNumber(20L)).getNumber());
    }

    @Test
    void mulIntegerByByteFromLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)200, number1.mul(new IntegerNumber(10)).getNumber());
        assertEquals((byte)400, number1.mul(new IntegerNumber(20)).getNumber());
    }

    @Test
    void mulShortByByte() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)200, number1.mul(new ShortNumber((short)10)).getNumber());
        assertEquals((byte)400, number1.mul(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void mulByteByByte() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)200, number1.mul(new ByteNumber((byte)10)).getNumber());
        assertEquals((byte)400, number1.mul(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void divByteByLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)2, number1.div(new LongNumber(10L)).getNumber());
        assertEquals((byte)1, number1.div(new LongNumber(20L)).getNumber());
    }

    @Test
    void divByteByInteger() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)2, number1.div(new IntegerNumber(10)).getNumber());
        assertEquals((byte)1, number1.div(new IntegerNumber(20)).getNumber());
    }

    @Test
    void divByteByShort() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)2, number1.div(new ShortNumber((short)10)).getNumber());
        assertEquals((byte)1, number1.div(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void modLongByLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)0, number1.mod(new LongNumber(4L)).getNumber());
        assertEquals((byte)2, number1.mod(new LongNumber(3L)).getNumber());
    }

    @Test
    void modIntegerByLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)0, number1.mod(new IntegerNumber(4)).getNumber());
        assertEquals((byte)2, number1.mod(new IntegerNumber(3)).getNumber());
    }

    @Test
    void modShortByLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)0, number1.mod(new ShortNumber((short)4)).getNumber());
        assertEquals((byte)2, number1.mod(new ShortNumber((short)3)).getNumber());
    }

    @Test
    void modByteFromLong() {
        ByteNumber number1 = new ByteNumber((byte)20);
        assertEquals((byte)0, number1.mod(new ByteNumber((byte)4)).getNumber());
        assertEquals((byte)2, number1.mod(new ByteNumber((byte)3)).getNumber());
    }

    @Test
    void abs() {
        assertEquals((byte)42, new ByteNumber((byte)42).abs().getNumber());
        assertEquals((byte)42, new ByteNumber((byte)-42).abs().getNumber());
    }

    @Test
    void sign() {
        assertEquals(1, new ByteNumber((byte)42).sign().getNumber());
        assertEquals(-1, new ByteNumber((byte)-42).sign().getNumber());
    }

    @Test
    void negate() {
        assertEquals((byte)-42, new ByteNumber((byte)42).negate().getNumber());
        assertEquals((byte)42, new ByteNumber((byte)-42).negate().getNumber());
    }

    @Test
    void getType() {
        assertEquals(Type.BYTE, new ByteNumber((byte)42).getType());
    }

    @Test
    void toType() {
        ByteNumber byteNumber = new ByteNumber((byte) 42);
        assertInstanceOf(ByteNumber.class, byteNumber.toType(Type.BYTE));
        assertInstanceOf(ShortNumber.class, byteNumber.toType(Type.SHORT));
        assertInstanceOf(IntegerNumber.class, byteNumber.toType(Type.INTEGER));
        assertInstanceOf(LongNumber.class, byteNumber.toType(Type.LONG));
        assertInstanceOf(FloatNumber.class, byteNumber.toType(Type.FLOAT));
        assertInstanceOf(DoubleNumber.class, byteNumber.toType(Type.DOUBLE));
    }
}
