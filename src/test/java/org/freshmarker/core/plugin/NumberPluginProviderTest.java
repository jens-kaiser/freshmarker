package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class NumberPluginProviderTest {
    private Map<BuiltInKey, BuiltIn> builtIns;
    private BuiltInKeyBuilder<TemplateNumber> builder;

    @BeforeEach
    public void setUp() {
        builder = new BuiltInKeyBuilder<>(TemplateNumber.class);
        builtIns = new HashMap<>();
        new NumberPluginProvider().registerBuildIn(builtIns);
    }

    @Test
    void computerBuiltIn() {
        BuiltIn builtIn = builtIns.get(builder.of("c"));
        assertEquals("42", builtIn.apply(new TemplateNumber(42), List.of(), null).toString());
        assertEquals("42.0", builtIn.apply(new TemplateNumber(42.0), List.of(), null).toString());
    }

    @Test
    void abs() {
        BuiltIn builtIn = builtIns.get(builder.of("abs"));
        assertEquals("42", builtIn.apply(new TemplateNumber(42), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber(-42), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber((byte)42), List.of(), null).toString());
        assertEquals("42", builtIn.apply(new TemplateNumber((byte)-42), List.of(), null).toString());
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
        BuiltIn builtIn = builtIns.get(builder.of("sign"));
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
        ProcessContext context = new ProcessContext(staticContext, baseEnvironment, null, null, Locale.GERMANY, null, null);
        BuiltIn builtIn = builtIns.get(builder.of("format"));
        assertEquals("42,00", builtIn.apply(new TemplateNumber(42.0), List.of(new TemplateString("%.2f")), context).toString());
        context.push(Locale.US);
        assertEquals("42.00", builtIn.apply(new TemplateNumber(42.0), List.of(new TemplateString("%.2f")), context).toString());
    }

    @Test
    void castInt() {
        BuiltIn builtIn = builtIns.get(builder.of("int"));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Integer.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castLong() {
        BuiltIn builtIn = builtIns.get(builder.of("long"));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Long.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castShort() {
        BuiltIn builtIn = builtIns.get(builder.of("short"));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Short.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castByte() {
        BuiltIn builtIn = builtIns.get(builder.of("byte"));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Byte.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castDouble() {
        BuiltIn builtIn = builtIns.get(builder.of("double"));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber((byte) 42), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Double.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    @Test
    void castFloat() {
        BuiltIn builtIn = builtIns.get(builder.of("float"));
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber((short) 42), List.of(), null));
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber(42), List.of(), null));
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber(42L), List.of(), null));
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber(42.0), List.of(), null));
        assertNumberType(Float.class, builtIn.apply(new TemplateNumber((float) 42.0), List.of(), null));
    }

    void assertNumberType(Class<?> type, TemplateObject object) {
        assertInstanceOf(type, ((TemplateNumber) object).getValue());
    }
}