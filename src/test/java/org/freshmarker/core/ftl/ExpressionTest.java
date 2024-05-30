package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.StandardOpenOption;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionTest {

    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
    }

    @Test
    void stringConcat() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${('abcdefg' + 'hijklmnop' + 'qrstuvwxyz')?upper_case}");
        assertEquals("test: ABCDEFGHIJKLMNOPQRSTUVWXYZ", template.process(Map.of()));
    }

    @Test
    void stringConcatWithVars() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${(prefix + 'hijklmnop' + suffix)?upper_case}");
        assertEquals("test: ABCDEFGHIJKLMNOPQRSTUVWXYZ", template.process(Map.of("prefix", "abcdefg", "suffix", "qrstuvwxyz")));
    }

    @Test
    void stringConcatWithEmptyVars() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${(prefix + 'hijklmnop' + suffix)?upper_case}");
        assertEquals("test: HIJKLMNOP", template.process(Map.of("prefix", "", "suffix", "")));
    }

    @ParameterizedTest
    @CsvSource({
            "2 > 1, true",
            "1 > 2, false",
            "3 >= 1, true",
            "1 >= 3, false",
            "2 < 1, false",
            "1 < 2, true",
            "3 <= 1, false",
            "1 <= 3, true",
            "2 gt 1, true",
            "1 gt 2, false",
            "3 gte 1, true",
            "1 gte 3, false",
            "2 lt 1, false",
            "1 lt 2, true",
            "3 lte 1, false",
            "1 lte 3, true",
    })
    void numberRelation(String expression, boolean result) throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${(" + expression + ")?c}");
        assertEquals("test: " + result, template.process(Map.of("prefix", "", "suffix", "")));
    }

    @ParameterizedTest
    @CsvSource({
            "1 == 1, true",
            "1 == 2, false",
            "1 = 1, true",
            "1 = 2, false",
            "1 != 1, false",
            "1 != 2, true",
    })
    void primitiveEquality(String expression, boolean result) throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${(" + expression + ")?c}");
        assertEquals("test: " + result, template.process(Map.of("prefix", "", "suffix", "")));
    }

    @ParameterizedTest
    @CsvSource({
            "!(1 < 1), true",
            "!(1 < 2), false",
            "!(1 > 1), true",
            "!(2 > 1), false",
            "!(1 <= 1), false",
            "!(2 >= 1), false",
    })
    void negatedRelation(String expression, boolean result) throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${(" + expression + ")?c}");
        assertEquals("test: " + result, template.process(Map.of("prefix", "", "suffix", "")));
    }

    @ParameterizedTest
    @CsvSource({
            "4 == (test?ordinal), true",
            "4 != (test?ordinal), false",
            "4 <= (test?ordinal), true",
            "3 < (test?ordinal), true",
            "4 >= (test?ordinal), true",
            "5 > (test?ordinal), true",
            "4 lte (test?ordinal), true",
            "3 lt (test?ordinal), true",
            "4 gte (test?ordinal), true",
            "5 gt (test?ordinal), true"
    })
    void relationWithEnum(String expression, boolean result) throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${(" + expression + ")?c}");
        assertEquals("test: " + result, template.process(Map.of("test", StandardOpenOption.CREATE)));
    }

    @ParameterizedTest
    @CsvSource({
            "true & true, true",
            "true & false, false",
            "false & true, false",
            "false & false, false",
            "true && true, true",
            "true && false, false",
            "false && true, false",
            "false && false, false",
            "false && 1, false",
            "true | true, true",
            "true | false, true",
            "false | true, true",
            "false | false, false",
            "true || true, true",
            "true || false, true",
            "false || true, true",
            "false || false, false",
            "true || 1, true",
            "true ^ true, false",
            "true ^ false, true",
            "false ^ true, true",
            "false ^ false, false",
    })
    void junction(String expression, boolean result) throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${(" + expression + ")?c}");
        assertEquals("test: " + result, template.process(Map.of("prefix", "", "suffix", "")));
    }
}
