package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LongNumberTest {

    @Test
    void addLongToLong() {
        LongNumber number1 = new LongNumber(10L);
        assertEquals(20L, number1.add(new LongNumber(10L)).getNumber());
        assertEquals(-10L, number1.add(new LongNumber(-20L)).getNumber());
    }

    @Test
    void addIntegerToLong() {
        LongNumber number1 = new LongNumber(10L);
        assertEquals(20L, number1.add(new IntegerNumber(10)).getNumber());
        assertEquals(-10L, number1.add(new IntegerNumber(-20)).getNumber());
    }

    @Test
    void addShortToLong() {
        LongNumber number1 = new LongNumber(10L);
        assertEquals(20L, number1.add(new ShortNumber((short)10)).getNumber());
        assertEquals(-10L, number1.add(new ShortNumber((short)-20)).getNumber());
    }

    @Test
    void addByteToLong() {
        LongNumber number1 = new LongNumber(10L);
        assertEquals(20L, number1.add(new ByteNumber((byte)10)).getNumber());
        assertEquals(-10L, number1.add(new ByteNumber((byte)-20)).getNumber());
    }

    @Test
    void subLongFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(10L, number1.sub(new LongNumber(10L)).getNumber());
        assertEquals(0L, number1.sub(new LongNumber(20L)).getNumber());
    }

    @Test
    void subIntegerFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(10L, number1.sub(new IntegerNumber(10)).getNumber());
        assertEquals(0L, number1.sub(new IntegerNumber(20)).getNumber());
    }

    @Test
    void subShortFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(10L, number1.sub(new ShortNumber((short)10)).getNumber());
        assertEquals(0L, number1.sub(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void subByteFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(10L, number1.sub(new ByteNumber((byte)10)).getNumber());
        assertEquals(0L, number1.sub(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void mulLongFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(200L, number1.mul(new LongNumber(10L)).getNumber());
        assertEquals(400L, number1.mul(new LongNumber(20L)).getNumber());
    }

    @Test
    void mulIntegerFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(200L, number1.mul(new IntegerNumber(10)).getNumber());
        assertEquals(400L, number1.mul(new IntegerNumber(20)).getNumber());
    }

    @Test
    void mulShortFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(200L, number1.mul(new ShortNumber((short)10)).getNumber());
        assertEquals(400L, number1.mul(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void mulByteFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(200L, number1.mul(new ByteNumber((byte)10)).getNumber());
        assertEquals(400L, number1.mul(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void divLongByLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(2L, number1.div(new LongNumber(10L)).getNumber());
        assertEquals(1L, number1.div(new LongNumber(20L)).getNumber());
    }

    @Test
    void divIntegerByLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(2L, number1.div(new IntegerNumber(10)).getNumber());
        assertEquals(1L, number1.div(new IntegerNumber(20)).getNumber());
    }

    @Test
    void divShortByLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(2L, number1.div(new ShortNumber((short)10)).getNumber());
        assertEquals(1L, number1.div(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void divByteFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(2L, number1.div(new ByteNumber((byte)10)).getNumber());
        assertEquals(1L, number1.div(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void modLongByLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(0L, number1.mod(new LongNumber(4L)).getNumber());
        assertEquals(2L, number1.mod(new LongNumber(3L)).getNumber());
    }

    @Test
    void modIntegerByLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(0L, number1.mod(new IntegerNumber(4)).getNumber());
        assertEquals(2L, number1.mod(new IntegerNumber(3)).getNumber());
    }

    @Test
    void modvShortByLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(0L, number1.mod(new ShortNumber((short)4)).getNumber());
        assertEquals(2L, number1.mod(new ShortNumber((short)3)).getNumber());
    }

    @Test
    void modByteFromLong() {
        LongNumber number1 = new LongNumber(20L);
        assertEquals(0L, number1.mod(new ByteNumber((byte)4)).getNumber());
        assertEquals(2L, number1.mod(new ByteNumber((byte)3)).getNumber());
    }

    @Test
    void abs() {
        assertEquals(42L, new LongNumber(42L).abs().getNumber());
        assertEquals(42L, new LongNumber(-42L).abs().getNumber());
    }

    @Test
    void sign() {
        assertEquals(1, new LongNumber(42L).sign().getNumber());
        assertEquals(-1, new LongNumber(-42L).sign().getNumber());
    }

    @Test
    void negate() {
        assertEquals(-42L, new LongNumber(42L).negate().getNumber());
        assertEquals(42L, new LongNumber(-42L).negate().getNumber());
    }

    @Test
    void getType() {
        assertEquals(Type.LONG, new LongNumber(42L).getType());
    }
}