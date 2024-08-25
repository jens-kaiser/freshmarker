package org.freshmarker.core.plugin;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.number.ByteNumber;
import org.freshmarker.core.model.number.DoubleNumber;
import org.freshmarker.core.model.number.FloatNumber;
import org.freshmarker.core.model.number.IntegerNumber;
import org.freshmarker.core.model.number.LongNumber;
import org.freshmarker.core.model.number.ShortNumber;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class NumberPluginProviderTest {
    @Test
    void computerBuiltIn() {
        assertEquals("42", NumberPluginProvider.computerBuiltIn(new TemplateNumber(42)).toString());
        assertEquals("42.0", NumberPluginProvider.computerBuiltIn(new TemplateNumber(42.0)).toString());
    }

    @Test
    void abs() {
        assertEquals("42", NumberPluginProvider.abs(new TemplateNumber(42)).toString());
        assertEquals("42", NumberPluginProvider.abs(new TemplateNumber(-42)).toString());
    }

    @Test
    void sign() {
        assertEquals("1", NumberPluginProvider.sign(new TemplateNumber(42)).toString());
        assertEquals("-1", NumberPluginProvider.sign(new TemplateNumber(-42)).toString());
        assertEquals("0", NumberPluginProvider.sign(new TemplateNumber(0)).toString());
    }

    @Test
    void format(@Mock Environment environment, @Mock BaseEnvironment baseEnvironment) {
        Mockito.when(environment.getLocale()).thenReturn(Locale.GERMANY, Locale.US);
        ProcessContext context = new ProcessContext(baseEnvironment, environment, null, null, null);
        assertEquals("42,00", NumberPluginProvider.format(new TemplateNumber(42.0), context, new TemplateString("%.2f")).toString());
        assertEquals("42.00", NumberPluginProvider.format(new TemplateNumber(42.0), context, new TemplateString("%.2f")).toString());
    }

    @Test
    void castInt() {
        assertInstanceOf(IntegerNumber.class, NumberPluginProvider.castInt(new TemplateNumber((byte) 42)).getValue());
        assertInstanceOf(IntegerNumber.class, NumberPluginProvider.castInt(new TemplateNumber((short) 42)).getValue());
        assertInstanceOf(IntegerNumber.class, NumberPluginProvider.castInt(new TemplateNumber(42)).getValue());
        assertInstanceOf(IntegerNumber.class, NumberPluginProvider.castInt(new TemplateNumber(42L)).getValue());
        assertInstanceOf(IntegerNumber.class, NumberPluginProvider.castInt(new TemplateNumber(42.0)).getValue());
        assertInstanceOf(IntegerNumber.class, NumberPluginProvider.castInt(new TemplateNumber((float) 42.0)).getValue());
    }

    @Test
    void castLong() {
        assertInstanceOf(LongNumber.class, NumberPluginProvider.castLong(new TemplateNumber((byte) 42)).getValue());
        assertInstanceOf(LongNumber.class, NumberPluginProvider.castLong(new TemplateNumber((short) 42)).getValue());
        assertInstanceOf(LongNumber.class, NumberPluginProvider.castLong(new TemplateNumber(42)).getValue());
        assertInstanceOf(LongNumber.class, NumberPluginProvider.castLong(new TemplateNumber(42L)).getValue());
        assertInstanceOf(LongNumber.class, NumberPluginProvider.castLong(new TemplateNumber(42.0)).getValue());
        assertInstanceOf(LongNumber.class, NumberPluginProvider.castLong(new TemplateNumber((float) 42.0)).getValue());
    }

    @Test
    void castShort() {
        assertInstanceOf(ShortNumber.class, NumberPluginProvider.castShort(new TemplateNumber((byte) 42)).getValue());
        assertInstanceOf(ShortNumber.class, NumberPluginProvider.castShort(new TemplateNumber((short) 42)).getValue());
        assertInstanceOf(ShortNumber.class, NumberPluginProvider.castShort(new TemplateNumber(42)).getValue());
        assertInstanceOf(ShortNumber.class, NumberPluginProvider.castShort(new TemplateNumber(42L)).getValue());
        assertInstanceOf(ShortNumber.class, NumberPluginProvider.castShort(new TemplateNumber(42.0)).getValue());
        assertInstanceOf(ShortNumber.class, NumberPluginProvider.castShort(new TemplateNumber((float) 42.0)).getValue());
    }

    @Test
    void castByte() {
        assertInstanceOf(ByteNumber.class, NumberPluginProvider.castByte(new TemplateNumber((byte) 42)).getValue());
        assertInstanceOf(ByteNumber.class, NumberPluginProvider.castByte(new TemplateNumber((short) 42)).getValue());
        assertInstanceOf(ByteNumber.class, NumberPluginProvider.castByte(new TemplateNumber(42)).getValue());
        assertInstanceOf(ByteNumber.class, NumberPluginProvider.castByte(new TemplateNumber(42L)).getValue());
        assertInstanceOf(ByteNumber.class, NumberPluginProvider.castByte(new TemplateNumber(42.0)).getValue());
        assertInstanceOf(ByteNumber.class, NumberPluginProvider.castByte(new TemplateNumber((float) 42.0)).getValue());
    }

    @Test
    void castDouble() {
        assertInstanceOf(DoubleNumber.class, NumberPluginProvider.castDouble(new TemplateNumber((byte) 42)).getValue());
        assertInstanceOf(DoubleNumber.class, NumberPluginProvider.castDouble(new TemplateNumber((short) 42)).getValue());
        assertInstanceOf(DoubleNumber.class, NumberPluginProvider.castDouble(new TemplateNumber(42)).getValue());
        assertInstanceOf(DoubleNumber.class, NumberPluginProvider.castDouble(new TemplateNumber(42L)).getValue());
        assertInstanceOf(DoubleNumber.class, NumberPluginProvider.castDouble(new TemplateNumber(42.0)).getValue());
        assertInstanceOf(DoubleNumber.class, NumberPluginProvider.castDouble(new TemplateNumber((float) 42.0)).getValue());
    }

    @Test
    void castFloat() {
        assertInstanceOf(FloatNumber.class, NumberPluginProvider.castFloat(new TemplateNumber((byte) 42)).getValue());
        assertInstanceOf(FloatNumber.class, NumberPluginProvider.castFloat(new TemplateNumber((short) 42)).getValue());
        assertInstanceOf(FloatNumber.class, NumberPluginProvider.castFloat(new TemplateNumber(42)).getValue());
        assertInstanceOf(FloatNumber.class, NumberPluginProvider.castFloat(new TemplateNumber(42L)).getValue());
        assertInstanceOf(FloatNumber.class, NumberPluginProvider.castFloat(new TemplateNumber(42.0)).getValue());
        assertInstanceOf(FloatNumber.class, NumberPluginProvider.castFloat(new TemplateNumber((float) 42.0)).getValue());
    }
}