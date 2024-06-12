package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortNumberTest {

    @Test
    void addLongToShort() {
        ShortNumber number1 = new ShortNumber((short)10);
        assertEquals((short)20, number1.add(new LongNumber(10L)).getNumber());
        assertEquals((short)-10, number1.add(new LongNumber(-20L)).getNumber());
    }

    @Test
    void addIntegerToShort() {
        ShortNumber number1 = new ShortNumber((short)10);
        assertEquals((short)20, number1.add(new IntegerNumber(10)).getNumber());
        assertEquals((short)-10, number1.add(new IntegerNumber(-20)).getNumber());
    }

    @Test
    void addShortToShort() {
        ShortNumber number1 = new ShortNumber((short)10);
        assertEquals((short)20, number1.add(new ShortNumber((short)10)).getNumber());
        assertEquals((short)-10, number1.add(new ShortNumber((short)-20)).getNumber());
    }

    @Test
    void addByteToShort() {
        ShortNumber number1 = new ShortNumber((short)10);
        assertEquals((short)20, number1.add(new ByteNumber((byte)10)).getNumber());
        assertEquals((short)-10, number1.add(new ByteNumber((byte)-20)).getNumber());
    }

    @Test
    void subLongFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)10, number1.sub(new LongNumber(10L)).getNumber());
        assertEquals((short)0, number1.sub(new LongNumber(20L)).getNumber());
    }

    @Test
    void subIntegerFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)10, number1.sub(new IntegerNumber(10)).getNumber());
        assertEquals((short)0, number1.sub(new IntegerNumber(20)).getNumber());
    }

    @Test
    void subShortFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)10, number1.sub(new ShortNumber((short)10)).getNumber());
        assertEquals((short)0, number1.sub(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void subByteFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)10, number1.sub(new ByteNumber((byte)10)).getNumber());
        assertEquals((short)0, number1.sub(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void mulLongFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)200, number1.mul(new LongNumber(10L)).getNumber());
        assertEquals((short)400, number1.mul(new LongNumber(20L)).getNumber());
    }

    @Test
    void mulIntegerFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)200, number1.mul(new IntegerNumber(10)).getNumber());
        assertEquals((short)400, number1.mul(new IntegerNumber(20)).getNumber());
    }

    @Test
    void mulShortFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)200, number1.mul(new ShortNumber((short)10)).getNumber());
        assertEquals((short)400, number1.mul(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void mulByteFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)200, number1.mul(new ByteNumber((byte)10)).getNumber());
        assertEquals((short)400, number1.mul(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void divLongByShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)2, number1.div(new LongNumber(10L)).getNumber());
        assertEquals((short)1, number1.div(new LongNumber(20L)).getNumber());
    }

    @Test
    void divIntegerByShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)2, number1.div(new IntegerNumber(10)).getNumber());
        assertEquals((short)1, number1.div(new IntegerNumber(20)).getNumber());
    }

    @Test
    void divShortByShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)2, number1.div(new ShortNumber((short)10)).getNumber());
        assertEquals((short)1, number1.div(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void divByteFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)2, number1.div(new ByteNumber((byte)10)).getNumber());
        assertEquals((short)1, number1.div(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void modLongByShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)0, number1.mod(new LongNumber(4L)).getNumber());
        assertEquals((short)2, number1.mod(new LongNumber(3L)).getNumber());
    }

    @Test
    void modIntegerByShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)0, number1.mod(new IntegerNumber(4)).getNumber());
        assertEquals((short)2, number1.mod(new IntegerNumber(3)).getNumber());
    }

    @Test
    void modvShortByShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)0, number1.mod(new ShortNumber((short)4)).getNumber());
        assertEquals((short)2, number1.mod(new ShortNumber((short)3)).getNumber());
    }

    @Test
    void modByteFromShort() {
        ShortNumber number1 = new ShortNumber((short)20);
        assertEquals((short)0, number1.mod(new ByteNumber((byte)4)).getNumber());
        assertEquals((short)2, number1.mod(new ByteNumber((byte)3)).getNumber());
    }

    @Test
    void abs() {
        assertEquals((short)42, new ShortNumber((short)42).abs().getNumber());
        assertEquals((short)42, new ShortNumber((short)-42).abs().getNumber());
    }

    @Test
    void sign() {
        assertEquals(1, new ShortNumber((short)42).sign().getNumber());
        assertEquals(-1, new ShortNumber((short)-42).sign().getNumber());
    }

    @Test
    void negate() {
        assertEquals((short)-42, new ShortNumber((short)42).negate().getNumber());
        assertEquals((short)42, new ShortNumber((short)-42).negate().getNumber());
    }

    @Test
    void getType() {
        assertEquals(Type.SHORT, new ShortNumber((short)42).getType());
    }
}
