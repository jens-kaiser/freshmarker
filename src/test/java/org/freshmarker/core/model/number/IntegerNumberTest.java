package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegerNumberTest {

    @Test
    void addLongToInteger() {
        IntegerNumber number1 = new IntegerNumber(10);
        assertEquals(20, number1.add(new LongNumber(10L)).getNumber());
        assertEquals(-10, number1.add(new LongNumber(-20L)).getNumber());
    }

    @Test
    void addIntegerToInteger() {
        IntegerNumber number1 = new IntegerNumber(10);
        assertEquals(20, number1.add(new IntegerNumber(10)).getNumber());
        assertEquals(-10, number1.add(new IntegerNumber(-20)).getNumber());
    }

    @Test
    void addShortToInteger() {
        IntegerNumber number1 = new IntegerNumber(10);
        assertEquals(20, number1.add(new ShortNumber((short)10)).getNumber());
        assertEquals(-10, number1.add(new ShortNumber((short)-20)).getNumber());
    }

    @Test
    void addByteToInteger() {
        IntegerNumber number1 = new IntegerNumber(10);
        assertEquals(20, number1.add(new ByteNumber((byte)10)).getNumber());
        assertEquals(-10, number1.add(new ByteNumber((byte)-20)).getNumber());
    }

    @Test
    void subLongFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(10, number1.sub(new LongNumber(10L)).getNumber());
        assertEquals(0, number1.sub(new LongNumber(20L)).getNumber());
    }

    @Test
    void subIntegerFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(10, number1.sub(new IntegerNumber(10)).getNumber());
        assertEquals(0, number1.sub(new IntegerNumber(20)).getNumber());
    }

    @Test
    void subShortFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(10, number1.sub(new ShortNumber((short)10)).getNumber());
        assertEquals(0, number1.sub(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void subByteFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(10, number1.sub(new ByteNumber((byte)10)).getNumber());
        assertEquals(0, number1.sub(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void mulLongFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(200, number1.mul(new LongNumber(10L)).getNumber());
        assertEquals(400, number1.mul(new LongNumber(20L)).getNumber());
    }

    @Test
    void mulIntegerFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(200, number1.mul(new IntegerNumber(10)).getNumber());
        assertEquals(400, number1.mul(new IntegerNumber(20)).getNumber());
    }

    @Test
    void mulShortFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(200, number1.mul(new ShortNumber((short)10)).getNumber());
        assertEquals(400, number1.mul(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void mulByteFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(200, number1.mul(new ByteNumber((byte)10)).getNumber());
        assertEquals(400, number1.mul(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void divLongByInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(2, number1.div(new LongNumber(10L)).getNumber());
        assertEquals(1, number1.div(new LongNumber(20L)).getNumber());
    }

    @Test
    void divIntegerByInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(2, number1.div(new IntegerNumber(10)).getNumber());
        assertEquals(1, number1.div(new IntegerNumber(20)).getNumber());
    }

    @Test
    void divShortByInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(2, number1.div(new ShortNumber((short)10)).getNumber());
        assertEquals(1, number1.div(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void divByteFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(2, number1.div(new ByteNumber((byte)10)).getNumber());
        assertEquals(1, number1.div(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void modLongByInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(0, number1.mod(new LongNumber(4L)).getNumber());
        assertEquals(2, number1.mod(new LongNumber(3L)).getNumber());
    }

    @Test
    void modIntegerByInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(0, number1.mod(new IntegerNumber(4)).getNumber());
        assertEquals(2, number1.mod(new IntegerNumber(3)).getNumber());
    }

    @Test
    void modvShortByInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(0, number1.mod(new ShortNumber((short)4)).getNumber());
        assertEquals(2, number1.mod(new ShortNumber((short)3)).getNumber());
    }

    @Test
    void modByteFromInteger() {
        IntegerNumber number1 = new IntegerNumber(20);
        assertEquals(0, number1.mod(new ByteNumber((byte)4)).getNumber());
        assertEquals(2, number1.mod(new ByteNumber((byte)3)).getNumber());
    }

    @Test
    void abs() {
        assertEquals(42, new IntegerNumber(42).abs().getNumber());
        assertEquals(42, new IntegerNumber(-42).abs().getNumber());
    }

    @Test
    void sign() {
        assertEquals(1, new IntegerNumber(42).sign().getNumber());
        assertEquals(-1, new IntegerNumber(-42).sign().getNumber());
    }

    @Test
    void negate() {
        assertEquals(-42, new IntegerNumber(42).negate().getNumber());
        assertEquals(42, new IntegerNumber(-42).negate().getNumber());
    }

    @Test
    void getType() {
        assertEquals(Type.INTEGER, new IntegerNumber(42).getType());
    }
}
