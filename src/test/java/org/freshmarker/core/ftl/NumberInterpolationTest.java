package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberInterpolationTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${a?c};test: 42",
            "test: ${b?c};test: 42",
            "test: ${c?c};test: 42",
            "test: ${d?c};test: 42",
    }, delimiterString = ";")
    void interpolationNumberC(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("a", 42, "b", 42L, "c", (short) 42, "d", (byte) 42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42};test: 42",
            "test: ${42.23};test: 42,23",
            "test: ${(-42)?abs};test: 42",
            "test: ${42?abs};test: 42",
            "test: ${(-0)?abs};test: 0",
            "test: ${0?abs};test: 0",
            "test: ${42?sign};test: 1",
            "test: ${(-42)?sign};test: -1",
            "test: ${(-0)?sign};test: 0",
            "test: ${0?sign};test: 0",
            "test: ${3.14159?abs};test: 3,142",
    }, delimiterString = ";")
    void interpolationConstant(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("pi", 3.14159)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42*x};test: 1.764",
            "test: ${42.0*x};test: 1.764",
            "test: ${42*10-0.5};test: 419,5",
            "test: ${42.23*10};test: 422,3",
            "test: ${x*x};test: -28",
            "test: ${y*y};test: 1.764",
            "test: ${x % 4};test: 2",
            "test: ${-x};test: -42",
            "test: ${+x};test: 42",
            "test: ${x+x};test: 84",
            "test: ${x-x};test: 0",
    }, delimiterString = ";")
    void interpolationByteExpression(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", (byte) 42, "y", 42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42*x};test: 1.764",
            "test: ${42.0*x};test: 1.764",
            "test: ${42*10-0.5};test: 419,5",
            "test: ${42.23*10};test: 422,3",
            "test: ${x*x};test: 1.764",
            "test: ${y*y};test: 1.764",
            "test: ${x % 4};test: 2",
            "test: ${-x};test: -42",
            "test: ${+x};test: 42",
            "test: ${x+x};test: 84",
            "test: ${x-x};test: 0",
    }, delimiterString = ";")
    void interpolationShortExpression(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", (short) 42, "y", 42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42*x};test: 1.764",
            "test: ${42.0*x};test: 1.764",
            "test: ${42*10-0.5};test: 419,5",
            "test: ${42.23*10};test: 422,3",
            "test: ${x*x};test: 1.764",
            "test: ${y*y};test: 1.764",
            "test: ${x % 4};test: 2",
            "test: ${-x};test: -42",
            "test: ${+x};test: 42",
            "test: ${x+x};test: 84",
            "test: ${x-x};test: 0",
    }, delimiterString = ";")
    void interpolationIntegerExpression(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x",  42, "y", 42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42*x};test: 1.764",
            "test: ${42.0*x};test: 1.764",
            "test: ${42*10-0.5};test: 419,5",
            "test: ${42.23*10};test: 422,3",
            "test: ${x*x};test: 1.764",
            "test: ${y*y};test: 1.764",
            "test: ${x % 4};test: 2",
            "test: ${-x};test: -42",
            "test: ${+x};test: 42",
            "test: ${x+x};test: 84",
            "test: ${x-x};test: 0",
    }, delimiterString = ";")
    void interpolationLongExpression(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x",  42L, "y", 42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42*x};test: 1.764",
            "test: ${42.0*x};test: 1.764",
            "test: ${42*10-0.5};test: 419,5",
            "test: ${42.23*10};test: 422,3",
            "test: ${x*x};test: 1.764",
            "test: ${y*y};test: 1.764",
            "test: ${x % 4};test: 2",
            "test: ${-x};test: -42",
            "test: ${+x};test: 42",
            "test: ${x+x};test: 84",
            "test: ${x-x};test: 0",
    }, delimiterString = ";")
    void interpolationFloatExpression(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x",  42.0f, "y", 42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42*x};test: 1.764",
            "test: ${42.0*x};test: 1.764",
            "test: ${42*10-0.5};test: 419,5",
            "test: ${42.23*10};test: 422,3",
            "test: ${x*x};test: 1.764",
            "test: ${y*y};test: 1.764",
            "test: ${x % 4};test: 2",
            "test: ${-x};test: -42",
            "test: ${+x};test: 42",
            "test: ${x+x};test: 84",
            "test: ${x-x};test: 0",
    }, delimiterString = ";")
    void interpolationDoubleExpression(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x",  42.0, "y", 42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${π?c};test: 3.141592653589793",
            "test: ${π?format('%10.4f')};test:     3,1416",
            "test: ${π?format('%.2f')};test: 3,14",
            "<#setting locale='en_US'>test: ${π?format('%.2f')};test: 3.14",
    }, delimiterString = ";")
    void format(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("π", Math.PI)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${x?byte?c};test: 42",
            "test: ${x?short?c};test: 42",
            "test: ${x?int?c};test: 42",
            "test: ${x?long?c};test: 42",
            "test: ${x?float?c};test: 42.0",
            "test: ${x?double?c};test: 42.0",
    }, delimiterString = ";")
    void interpolationIntegerCast(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", 42, "y", 420000)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: I Ⅰ,1",
            "test: II ⅠⅠ,2",
            "test: IV ⅠⅤ,4",
            "test: VI ⅤⅠ,6",
            "test: IX ⅠⅩ,9",
            "test: X Ⅹ,10",
            "test: XIV ⅩⅠⅤ,14",
            "test: MMXII ⅯⅯⅩⅠⅠ,2012",
    })
    void interpolationRoman(String expected, int value) throws ParseException {
        Template template = configuration.getTemplate("roman", "test: ${x?roman} ${x?utf_roman}");
        assertEquals(expected, template.process(Map.of("x", value)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${x?roman},0",
            "test: ${x?utf_roman},0",
            "test: ${x?clock_roman},0",
            "test: ${x?roman},4000",
            "test: ${x?utf_roman},4000",
            "test: ${x?clock_roman},13",
    })
    void interpolationInvalidRoman(String input, int value) throws ParseException {
        Template template = configuration.getTemplate("roman", input);
        Map<String, Object> model = Map.of("x", value);
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void interpolationClockRoman() throws ParseException {
        Template template = configuration.getTemplate("roman", "test:<#list 1..12 as c with l> ${l?clock_roman?lower_case} ${c?clock_roman}</#list>");
        assertEquals("test: ⅰ Ⅰ ⅱ Ⅱ ⅲ Ⅲ ⅳ Ⅳ ⅴ Ⅴ ⅵ Ⅵ ⅶ Ⅶ ⅷ Ⅷ ⅸ Ⅸ ⅹ Ⅹ ⅺ Ⅺ ⅻ Ⅻ", template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "de;test: ${0?h};test: 0",
            "de;test: ${1?h};test: eins",
            "de;test: ${2?h};test: zwei",
            "de;test: ${10?h};test: 10",
            "de;test: ${1.0?h};test: 1",
            "en;test: ${0?h};test: 0",
            "en;test: ${1?h};test: one",
            "en;test: ${2?h};test: two",
            "en;test: ${10?h};test: 10",
            "en;test: ${1.0?h};test: 1",
            "en;test: ${0?h};test: 0",
            "fr;test: ${1?h};test: un",
            "fr;test: ${2?h};test: deux",
            "fr;test: ${10?h};test: 10",
            "fr;test: ${1.0?h};test: 1",
    }, delimiterString = ";")
    void interpolationHuman(Locale locale, String templateSource, String expected) throws ParseException {
        configuration.setLocale(locale);
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }
}