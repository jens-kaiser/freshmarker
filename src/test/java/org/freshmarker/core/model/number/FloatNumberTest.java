package org.freshmarker.core.model.number;

import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FloatNumberTest {

    @Test
    void addLongToFloat() {
        FloatNumber number = new FloatNumber((float)10.0);
        assertEquals((float)20.0, number.add(new LongNumber(10L)).getNumber());
        assertEquals((float)-10.0, number.add(new LongNumber(-20L)).getNumber());
    }

    @Test
    void addIntegerToFloat() {
        FloatNumber number = new FloatNumber((float)10.0);
        assertEquals((float)20.0, number.add(new IntegerNumber(10)).getNumber());
        assertEquals((float)-10.0, number.add(new IntegerNumber(-20)).getNumber());
    }

    @Test
    void addShortToFloat() {
        FloatNumber number = new FloatNumber((float)10.0);
        assertEquals((float)20.0, number.add(new ShortNumber((short)10)).getNumber());
        assertEquals((float)-10.0, number.add(new ShortNumber((short)-20)).getNumber());
    }

    @Test
    void addByteToFloat() {
        FloatNumber number = new FloatNumber((float)10.0);
        assertEquals((float)20.0, number.add(new ByteNumber((byte)10)).getNumber());
        assertEquals((float)-10.0, number.add(new ByteNumber((byte)-20)).getNumber());
    }

    @Test
    void subLongFromFloat() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)10.0, number.sub(new LongNumber(10L)).getNumber());
        assertEquals((float)0.0, number.sub(new LongNumber(20L)).getNumber());
    }

    @Test
    void subIntegerFromFloat() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)10.0, number.sub(new IntegerNumber(10)).getNumber());
        assertEquals((float)0.0, number.sub(new IntegerNumber(20)).getNumber());
    }

    @Test
    void subShortFromFloat() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)10.0, number.sub(new ShortNumber((short)10)).getNumber());
        assertEquals((float)0.0, number.sub(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void subByteFromFloat() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)10.0, number.sub(new ByteNumber((byte)10)).getNumber());
        assertEquals((float)0.0, number.sub(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void mulDoubleByLong() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)200.0, number.mul(new LongNumber(10L)).getNumber());
        assertEquals((float)400.0, number.mul(new LongNumber(20L)).getNumber());
    }

    @Test
    void mulDoubleByInteger() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)200.0, number.mul(new IntegerNumber(10)).getNumber());
        assertEquals((float)400.0, number.mul(new IntegerNumber(20)).getNumber());
    }

    @Test
    void mulDoubleByShort() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)200.0, number.mul(new ShortNumber((short)10)).getNumber());
        assertEquals((float)400.0, number.mul(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void mulDoubleByByte() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)200.0, number.mul(new ByteNumber((byte)10)).getNumber());
        assertEquals((float)400.0, number.mul(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void divDoubleByLong() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)2.0, number.div(new LongNumber(10L)).getNumber());
        assertEquals((float)1.0, number.div(new LongNumber(20L)).getNumber());
    }

    @Test
    void divDoubleByInteger() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)2.0, number.div(new IntegerNumber(10)).getNumber());
        assertEquals((float)1.0, number.div(new IntegerNumber(20)).getNumber());
    }

    @Test
    void divDoubleByShort() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)2.0, number.div(new ShortNumber((short)10)).getNumber());
        assertEquals((float)1.0, number.div(new ShortNumber((short)20)).getNumber());
    }

    @Test
    void divByteFromInteger() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)2.0, number.div(new ByteNumber((byte)10)).getNumber());
        assertEquals((float)1.0, number.div(new ByteNumber((byte)20)).getNumber());
    }

    @Test
    void modDoubleByLong() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)0.0, number.mod(new LongNumber(4L)).getNumber());
        assertEquals((float)2.0, number.mod(new LongNumber(3L)).getNumber());
    }

    @Test
    void modDoubleByFloat() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)0.0, number.mod(new IntegerNumber(4)).getNumber());
        assertEquals((float)2.0, number.mod(new IntegerNumber(3)).getNumber());
    }

    @Test
    void modDoubleByShort() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)0.0, number.mod(new ShortNumber((short)4)).getNumber());
        assertEquals((float)2.0, number.mod(new ShortNumber((short)3)).getNumber());
    }

    @Test
    void modDoubleByByte() {
        FloatNumber number = new FloatNumber((float)20.0);
        assertEquals((float)0.0, number.mod(new ByteNumber((byte)4)).getNumber());
        assertEquals((float)2.0, number.mod(new ByteNumber((byte)3)).getNumber());
    }

    @Test
    void abs() {
        assertEquals((float)42.0, new FloatNumber((float)42.0).abs().getNumber());
        assertEquals((float)42.0, new FloatNumber((float)-42.0).abs().getNumber());
    }

    @Test
    void sign() {
        assertEquals(1, new FloatNumber((float)42.0).sign().getNumber());
        assertEquals(-1, new FloatNumber((float)-42.0).sign().getNumber());
    }

    @Test
    void negate() {
        assertEquals((float)-42.0, new FloatNumber((float)42.0).negate().getNumber());
        assertEquals((float)42.0, new FloatNumber((float)-42.0).negate().getNumber());
    }

    @Test
    void getType() {
        assertEquals(Type.FLOAT, new FloatNumber((float)42.0).getType());
    }

    @Test
    void toType() {
        FloatNumber doubleNumber = new FloatNumber((float)42.0);
        assertInstanceOf(ByteNumber.class, doubleNumber.toType(Type.BYTE));
        assertInstanceOf(ShortNumber.class, doubleNumber.toType(Type.SHORT));
        assertInstanceOf(IntegerNumber.class, doubleNumber.toType(Type.INTEGER));
        assertInstanceOf(LongNumber.class, doubleNumber.toType(Type.LONG));
        assertInstanceOf(FloatNumber.class, doubleNumber.toType(Type.FLOAT));
        assertInstanceOf(DoubleNumber.class, doubleNumber.toType(Type.DOUBLE));
    }
}
