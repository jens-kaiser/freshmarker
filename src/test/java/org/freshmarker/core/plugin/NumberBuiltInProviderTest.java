package org.freshmarker.core.plugin;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class NumberBuiltInProviderTest {
    private Map<String, BuiltIn> builtIns;

    @BeforeEach
    void setUp() {
        builtIns = new NumberBuiltInProvider().provideBuiltInRegister().asMap().get(TemplateNumber.class);
    }

    @Test
    void computerBuiltIn() {
        BuiltIn builtIn = builtIns.get("c");
        assertEquals("42", builtIn.apply(new TemplateNumber(42), List.of(), null).toString());
        assertEquals("42.0", builtIn.apply(new TemplateNumber(42.0), List.of(), null).toString());
    }

    @Test
    void abs() {
        BuiltIn builtIn = builtIns.get("abs");
        assertEquals("42", builtIn.apply(new TemplateNumber(42), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber(-42), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber((byte) 42), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber((byte) -42), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber((short) 42), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber((short) -42), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber(42L), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber(-42L), List.of(), null).toString());
        assertEquals("42.0", builtIn.apply(new TemplateNumber(42.0), List.of(), null).toString());
        assertEquals("42.0", builtIn.apply(new TemplateNumber(-42.0), List.of(), null).toString());
        assertEquals("42.0", builtIn.apply(new TemplateNumber(42.0f), List.of(), null).toString());
        assertEquals("42.0", builtIn.apply(new TemplateNumber(-42.0f), List.of(), null).toString());
    }

    @Test
    void sign() {
        BuiltIn builtIn = builtIns.get("sign");
        assertEquals("1", builtIn.apply(new TemplateNumber(42), List.of(), null).toString());
        assertEquals("-1", builtIn.apply(new TemplateNumber(-42), List.of(), null).toString());
        assertEquals("0", builtIn.apply(new TemplateNumber(0), List.of(), null).toString());
        assertEquals("1", builtIn.apply(new TemplateNumber((byte) 42), List.of(), null).toString());
        assertEquals("-1", builtIn.apply(new TemplateNumber((byte) -42), List.of(), null).toString());
        assertEquals("0", builtIn.apply(new TemplateNumber((byte) 0), List.of(), null).toString());
        assertEquals("1", builtIn.apply(new TemplateNumber((short) 42), List.of(), null).toString());
        assertEquals("-1", builtIn.apply(new TemplateNumber((short) -42), List.of(), null).toString());
        assertEquals("0", builtIn.apply(new TemplateNumber((short) 0), List.of(), null).toString());
        assertEquals("1.0", builtIn.apply(new TemplateNumber(42.0), List.of(), null).toString());
        assertEquals("-1.0", builtIn.apply(new TemplateNumber(-42.0), List.of(), null).toString());
        assertEquals("0.0", builtIn.apply(new TemplateNumber(0.0), List.of(), null).toString());
        assertEquals("1.0", builtIn.apply(new TemplateNumber(42.0f), List.of(), null).toString());
        assertEquals("-1.0", builtIn.apply(new TemplateNumber(-42.0f), List.of(), null).toString());
        assertEquals("0.0", builtIn.apply(new TemplateNumber(0.0f), List.of(), null).toString());
    }

    @Test
    void format(@Mock StaticContext staticContext, @Mock BaseEnvironment baseEnvironment) {
        ProcessContext context = new ProcessContext(staticContext, baseEnvironment, Map.of(), null, Locale.GERMANY, null, null, Map.of(), null, null);
        BuiltIn builtIn = builtIns.get("format");
        assertEquals("42,00", builtIn.apply(new TemplateNumber(42.0), List.of(new TemplateString("%.2f")), context).toString());
        context.push(Locale.US);
        assertEquals("42.00", builtIn.apply(new TemplateNumber(42.0), List.of(new TemplateString("%.2f")), context).toString());
    }

    @Test
    void castInt() {
        BuiltIn builtIn = builtIns.get("int");
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castLong() {
        BuiltIn builtIn = builtIns.get("long");
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castShort() {
        BuiltIn builtIn = builtIns.get("short");
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castByte() {
        BuiltIn builtIn = builtIns.get("byte");
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castDouble() {
        BuiltIn builtIn = builtIns.get("double");
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castFloat() {
        BuiltIn builtIn = builtIns.get("float");
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castBigInteger() {
        BuiltIn builtIn = builtIns.get("big_integer");
        assertNumberType(BigInteger.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(BigInteger.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(BigInteger.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(BigInteger.class, builtIn.apply(new TemplateNumber(new BigInteger("42")), List.of(), null));
        assertNumberType(BigInteger.class, builtIn.apply(new TemplateNumber(new BigDecimal("42")), List.of(), null));
        assertNumberType(BigInteger.class, builtIn.apply(new TemplateNumber(new AtomicInteger(42), Type.INTEGER), List.of(), null));
        assertNumberType(BigInteger.class, builtIn.apply(new TemplateNumber(new AtomicLong(42), Type.LONG), List.of(), null));
    }

    @Test
    void castBigDecimal() {
        BuiltIn builtIn = builtIns.get("big_decimal");
        assertNumberType(BigDecimal.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(BigDecimal.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(BigDecimal.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(BigDecimal.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(BigDecimal.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
        assertNumberType(BigDecimal.class, builtIn.apply(new TemplateNumber(new BigInteger("42")), List.of(), null));
        assertNumberType(BigDecimal.class, builtIn.apply(new TemplateNumber(new BigDecimal("42")), List.of(), null));
        assertNumberType(BigDecimal.class, builtIn.apply(new TemplateNumber(new AtomicInteger(42), Type.INTEGER), List.of(), null));
        assertNumberType(BigDecimal.class, builtIn.apply(new TemplateNumber(new AtomicLong(42), Type.LONG), List.of(), null));
    }

    void assertNumberType(Class<?> type, TemplateObject object) {
        assertInstanceOf(type, ((TemplateNumber) object).getValue());
    }
}