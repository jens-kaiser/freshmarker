package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberInterpolationTest {
    private TemplateBuilder templateBuilder;

    @BeforeEach
    void setUp() {
        templateBuilder = new Configuration().builder().withLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${a?c};test: 42",
            "test: ${b?c};test: 42",
            "test: ${c?c};test: 42",
            "test: ${d?c};test: 42",
            "test: ${e?c};test: 42.0",
            "test: ${f?c};test: 42.0",
            "test: ${g?c};test: 42",
            "test: ${h?c};test: 42",
    }, delimiterString = ";")
    void interpolationNumberC(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("a", 42, "b", 42L, "c", (short) 42, "d", (byte) 42,
                "e", (float) 42, "f", (double) 42, "g", new BigInteger("42"), "h", new BigDecimal("42"))));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42};test: 42",
            "test: ${-42};test: -42",
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
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("pi", 3.14159)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${b+b};test: 84",
            "test: ${s+s};test: 84",
            "test: ${i+i};test: 84",
            "test: ${l+l};test: 84",
            "test: ${f+f};test: 84",
            "test: ${d+d};test: 84",
            "test: ${bd+bd};test: 84",
            "test: ${bi+bi};test: 84",
            "test: ${ai+ai};test: 84",
            "test: ${al+al};test: 84",

            "test: ${b-b};test: 0",
            "test: ${s-s};test: 0",
            "test: ${i-i};test: 0",
            "test: ${l-l};test: 0",
            "test: ${f-f};test: 0",
            "test: ${d-d};test: 0",
            "test: ${bd-bd};test: 0",
            "test: ${bi-bi};test: 0",
            "test: ${ai-ai};test: 0",
            "test: ${al-al};test: 0",

            "test: ${b*b};test: -28",
            "test: ${s*s};test: 1.764",
            "test: ${i*i};test: 1.764",
            "test: ${l*l};test: 1.764",
            "test: ${f*f};test: 1.764",
            "test: ${d*d};test: 1.764",
            "test: ${bd*bd};test: 1.764",
            "test: ${bi*bi};test: 1.764",
            "test: ${ai*ai};test: 1.764",
            "test: ${al*al};test: 1.764",

            "test: ${b/b};test: 1",
            "test: ${s/s};test: 1",
            "test: ${i/i};test: 1",
            "test: ${l/l};test: 1",
            "test: ${f/f};test: 1",
            "test: ${d/d};test: 1",
            "test: ${bd/bd};test: 1",
            "test: ${bi/bi};test: 1",
            "test: ${ai/ai};test: 1",
            "test: ${al/al};test: 1",

            "test: ${b%b};test: 0",
            "test: ${s%s};test: 0",
            "test: ${i%i};test: 0",
            "test: ${l%l};test: 0",
            "test: ${f%f};test: 0",
            "test: ${d%d};test: 0",
            "test: ${bd%bd};test: 0",
            "test: ${bi%bi};test: 0",
            "test: ${ai%ai};test: 0",
            "test: ${al%al};test: 0",

            "test: ${b%5};test: 2",
            "test: ${s%5};test: 2",
            "test: ${i%5};test: 2",
            "test: ${l%5};test: 2",
            "test: ${f%5};test: 2",
            "test: ${d%5};test: 2",
            "test: ${bd%5};test: 2",
            "test: ${bi%5};test: 2",
            "test: ${ai%5};test: 2",
            "test: ${al%5};test: 2",

            "test: ${+b};test: 42",
            "test: ${+s};test: 42",
            "test: ${+i};test: 42",
            "test: ${+l};test: 42",
            "test: ${+f};test: 42",
            "test: ${+d};test: 42",
            "test: ${+bd};test: 42",
            "test: ${+bi};test: 42",
            "test: ${+ai};test: 42",
            "test: ${+al};test: 42",

            "test: ${-b};test: -42",
            "test: ${-s};test: -42",
            "test: ${-i};test: -42",
            "test: ${-l};test: -42",
            "test: ${-f};test: -42",
            "test: ${-d};test: -42",
            "test: ${-bd};test: -42",
            "test: ${-bi};test: -42",
            "test: ${-ai};test: -42",
            "test: ${-al};test: -42",
    }, delimiterString = ";")
    void additiveMultiplicative(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        Map<String, Object> model = Map.of(
                "b", (byte) 42, "s", (short) 42, "i", 42, "l", 42L, "f", 42.0f, "d", 42.0,
                "bd", new BigDecimal("42.0"), "bi", new BigInteger("42"), "ai", new AtomicInteger(42), "al", new AtomicLong(42)
        );
        assertEquals(expected, template.process(model));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${b?abs};test: 42",
            "test: ${s?abs};test: 42",
            "test: ${i?abs};test: 42",
            "test: ${l?abs};test: 42",
            "test: ${f?abs};test: 42",
            "test: ${d?abs};test: 42",
            "test: ${bd?abs};test: 42",
            "test: ${bi?abs};test: 42",
            "test: ${ai?abs};test: 42",
            "test: ${al?abs};test: 42",

            "test: ${nb?abs};test: 42",
            "test: ${ns?abs};test: 42",
            "test: ${ni?abs};test: 42",
            "test: ${nl?abs};test: 42",
            "test: ${nf?abs};test: 42",
            "test: ${nd?abs};test: 42",
            "test: ${nbd?abs};test: 42",
            "test: ${nbi?abs};test: 42",
            "test: ${nai?abs};test: 42",
            "test: ${nal?abs};test: 42",

            "test: ${b?sign};test: 1",
            "test: ${s?sign};test: 1",
            "test: ${i?sign};test: 1",
            "test: ${l?sign};test: 1",
            "test: ${f?sign};test: 1",
            "test: ${d?sign};test: 1",
            "test: ${bd?sign};test: 1",
            "test: ${bi?sign};test: 1",
            "test: ${ai?sign};test: 1",
            "test: ${al?sign};test: 1",

            "test: ${nb?sign};test: -1",
            "test: ${ns?sign};test: -1",
            "test: ${ni?sign};test: -1",
            "test: ${nl?sign};test: -1",
            "test: ${nf?sign};test: -1",
            "test: ${nd?sign};test: -1",
            "test: ${nbd?sign};test: -1",
            "test: ${nbi?sign};test: -1",
            "test: ${nai?sign};test: -1",
            "test: ${nal?sign};test: -1",
    }, delimiterString = ";")
    void absAndSign(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        Map<String, Object> model = Map.ofEntries(Map.entry("b", (byte) 42), Map.entry("s", (short) 42), Map.entry("i", 42), Map.entry("l", 42L),
                Map.entry("f", 42.0f), Map.entry("d", 42.0), Map.entry("bd", new BigDecimal("42.0")), Map.entry("bi", new BigInteger("42")),
                Map.entry("nb", (byte) -42), Map.entry("ns", (short) -42), Map.entry("ni", -42), Map.entry("nl", -42L), Map.entry("nf", -42.0f),
                Map.entry("nd", -42.0), Map.entry("nbd", new BigDecimal("-42.0")), Map.entry("nbi", new BigInteger("-42")),
                Map.entry("ai", new AtomicInteger(42)), Map.entry("al", new AtomicLong(42)),
                Map.entry("nai", new AtomicInteger(-42)), Map.entry("nal", new AtomicLong(-42))

        );
        assertEquals(expected, template.process(model));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${z < x};test: yes",
            "test: ${z <= x};test: yes",
            "test: ${z > x};test: no",
            "test: ${z >= x};test: no",
    }, delimiterString = ";")
    void interpolationByteExpression(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", (byte) 42, "z", (byte) -42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${z < x};test: yes",
            "test: ${z <= x};test: yes",
            "test: ${z > x};test: no",
            "test: ${z >= x};test: no",
    }, delimiterString = ";")
    void interpolationShortExpression(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", (short) 42, "z", (short) -42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${0+x};test: 42",
            "test: ${x+0};test: 42",
            "test: ${x-0};test: 42",
            "test: ${x*1};test: 42",
            "test: ${z < x};test: yes",
            "test: ${z <= x};test: yes",
            "test: ${z > x};test: no",
            "test: ${z >= x};test: no",
    }, delimiterString = ";")
    void interpolationIntegerExpression(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", 42, "z", -42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${z < x};test: yes",
            "test: ${z <= x};test: yes",
            "test: ${z > x};test: no",
            "test: ${z >= x};test: no",
    }, delimiterString = ";")
    void interpolationLongExpression(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", 42L, "z", -42L)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${z < x};test: yes",
            "test: ${z <= x};test: yes",
            "test: ${z > x};test: no",
            "test: ${z >= x};test: no",
    }, delimiterString = ";")
    void interpolationFloatExpression(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", 42.0f, "z", -42.0f)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${z < x};test: yes",
            "test: ${z <= x};test: yes",
            "test: ${z > x};test: no",
            "test: ${z >= x};test: no",
    }, delimiterString = ";")
    void interpolationDoubleExpression(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", 42, "z", -42.0)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${π?c};test: 3.141592653589793",
            "test: ${π?format('%10.4f')};test:     3,1416",
            "test: ${π?format('%.2f')};test: 3,14",
            "<#setting locale='en_US'>test: ${π?format('%.2f')};test: 3.14",
    }, delimiterString = ";")
    void format(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("π", Math.PI)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${a?byte?c} ${a?short?c} ${a?int?c} ${a?int?c};test: 42 42 42 42",
            "test: ${a?float?c} ${a?double?c};test: 42.0 42.0",
            "test: ${a?big_integer?c} ${a?big_decimal?c};test: 42 42",
            "test: ${b?byte?c} ${b?short?c} ${b?int?c} ${b?int?c};test: 42 42 42 42",
            "test: ${c?byte?c} ${c?short?c} ${c?int?c} ${c?int?c};test: 42 42 42 42",
            "test: ${d?byte?c} ${d?short?c} ${d?int?c} ${d?int?c};test: 42 42 42 42",
    }, delimiterString = ";")
    void interpolationIntegerCast(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("a", (byte) 42, "b", (short) 42, "c", 42, "d", 42L)));
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
        Template template = templateBuilder.getTemplate("roman", "test: ${x?roman} ${x?utf_roman}");
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
        Template template = templateBuilder.getTemplate("roman", input);
        Map<String, Object> model = Map.of("x", value);
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void interpolationClockRoman() throws ParseException {
        Template template = templateBuilder.getTemplate("roman", "test:<#list 1..12 as c with l> ${l?clock_roman?lower_case} ${c?clock_roman}</#list>");
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
        Template template = templateBuilder.withLocale(locale).getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${z < x};test: yes",
            "test: ${z <= x};test: yes",
            "test: ${z > x};test: no",
            "test: ${z >= x};test: no",
    }, delimiterString = ";")
    void interpolationBigIntegerExpression(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", new BigInteger("42"), "z", new BigInteger("-42"))));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${z < x};test: yes",
            "test: ${z <= x};test: yes",
            "test: ${z > x};test: no",
            "test: ${z >= x};test: no",
    }, delimiterString = ";")
    void interpolationBigDecimalExpression(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", new BigDecimal("42"), "z", new BigDecimal("-42"))));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "${a?min(40)};40",
            "${b?min(40)};40",
            "${c?min(40)};40",
            "${d?min(40)};40",
            "${e?min(40)};40",
            "${f?min(40)};40",
            "${g?min(40)};40",
            "${h?min(40)};40",

            "${a?min(45)};42",
            "${b?min(45)};42",
            "${c?min(45)};42",
            "${d?min(45)};42",
            "${e?min(45)};42",
            "${f?min(45)};42",
            "${g?min(45)};42",
            "${h?min(45)};42",

            "${a?max(40)};42",
            "${b?max(40)};42",
            "${c?max(40)};42",
            "${d?max(40)};42",
            "${e?max(40)};42",
            "${f?max(40)};42",
            "${g?max(40)};42",
            "${h?max(40)};42",

            "${a?max(45)};45",
            "${b?max(45)};45",
            "${c?max(45)};45",
            "${d?max(45)};45",
            "${e?max(45)};45",
            "${f?max(45)};45",
            "${g?max(45)};45",
            "${h?max(45)};45",
    }, delimiterString = ";")
    void interpolationMinMax(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("a", 42, "b", 42L, "c", (short) 42, "d", (byte) 42,
                "e", (float) 42, "f", (double) 42, "g", new BigInteger("42"), "h", new BigDecimal("42"))));
    }


    @ParameterizedTest
    @CsvSource(value = {
            "${a?max('45')}",
            "${b?max('45')}",
            "${c?max('45')}",
            "${d?max('45')}",
            "${e?max('45')}",
            "${f?max('45')}",
            "${g?max('45')}",
            "${h?max('45')}",

            "${a?max}",
            "${b?max}",
            "${c?max}",
            "${d?max}",
            "${e?max}",
            "${f?max}",
            "${g?max}",
            "${h?max}",

            "${a?min('45')}",
            "${b?min('45')}",
            "${c?min('45')}",
            "${d?min('45')}",
            "${e?min('45')}",
            "${f?min('45')}",
            "${g?min('45')}",
            "${h?min('45')}",

            "${a?min}",
            "${b?min}",
            "${c?min}",
            "${d?min}",
            "${e?min}",
            "${f?min}",
            "${g?min}",
            "${h?min}",
    }, delimiterString = ";")
    void interpolationInvalidMinMax(String templateSource) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        Map<String, Object> model = Map.of("a", 42, "b", 42L, "c", (short) 42, "d", (byte) 42,
                "e", (float) 42, "f", (double) 42, "g", new BigInteger("42"), "h", new BigDecimal("42"));
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "${a?clamp(45,50)};45",
            "${b?clamp(45,50)};45",
            "${c?clamp(45,50)};45",
            "${d?clamp(45,50)};45",
            "${e?clamp(45,50)};45",
            "${f?clamp(45,50)};45",
            "${g?clamp(45,50)};45",
            "${h?clamp(45,50)};45",

            "${a?clamp(30,35)};35",
            "${b?clamp(30,35)};35",
            "${c?clamp(30,35)};35",
            "${d?clamp(30,35)};35",
            "${e?clamp(30,35)};35",
            "${f?clamp(30,35)};35",
            "${g?clamp(30,35)};35",
            "${h?clamp(30,35)};35",

            "${a?clamp(40,50)};42",
            "${b?clamp(40,50)};42",
            "${c?clamp(40,50)};42",
            "${d?clamp(40,50)};42",
            "${e?clamp(40,50)};42",
            "${f?clamp(40,50)};42",
            "${g?clamp(40,50)};42",
            "${h?clamp(40,50)};42",

            "${a?clamp(a,a)};42",
            "${b?clamp(b,b)};42",
            "${c?clamp(c,c)};42",
            "${d?clamp(d,d)};42",
            "${e?clamp(e,e)};42",
            "${f?clamp(f,f)};42",
            "${g?clamp(g,g)};42",
            "${h?clamp(h,h)};42",
    }, delimiterString = ";")
    void interpolationClamp(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("a", 42, "b", 42L, "c", (short) 42, "d", (byte) 42,
                "e", (float) 42, "f", (double) 42, "g", new BigInteger("42"), "h", new BigDecimal("42"))));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "${a?clamp(40,'45')}",
            "${b?clamp(40,'45')}",
            "${c?clamp(40,'45')}",
            "${d?clamp(40,'45')}",
            "${e?clamp(40,'45')}",
            "${f?clamp(40,'45')}",
            "${g?clamp(40,'45')}",
            "${h?clamp(40,'45')}",

            "${a?clamp}",
            "${b?clamp}",
            "${c?clamp}",
            "${d?clamp}",
            "${e?clamp}",
            "${f?clamp}",
            "${g?clamp}",
            "${h?clamp}",

            "${a?clamp(40,30)}",
            "${b?clamp(40,30)}",
            "${c?clamp(40,30)}",
            "${d?clamp(40,30)}",
            "${e?clamp(40,30)}",
            "${f?clamp(40,30)}",
            "${g?clamp(40,30)}",
            "${h?clamp(40,30)}",
    }, delimiterString = ";")
    void interpolationInvalidClamp(String templateSource) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        Map<String, Object> model = Map.of("a", 42, "b", 42L, "c", (short) 42, "d", (byte) 42,
                "e", (float) 42, "f", (double) 42, "g", new BigInteger("42"), "h", new BigDecimal("42"));
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @ParameterizedTest
    @CsvSource({ "0123456789,47", "①②③④⑤⑥⑦⑧⑨⑩,9311", "ᚠᚡᚢᚣᚤᚥᚦᚧᚨᚩ,5791"})
    void interpolateUnicodeWithOffset(String expected, int offset) {
        Template template = templateBuilder.getTemplate("test", "<#list 1..10 as c>${c?unicode(o)}</#list>");
        assertEquals(expected, template.process(Map.of("o", offset)));
    }

    @Test
    void interpolateUnicode() {
        Template template = templateBuilder.getTemplate("test", "<#list 48..57 as c>${c?unicode}</#list>");
        assertEquals("0123456789", template.process(Map.of()));
    }
}