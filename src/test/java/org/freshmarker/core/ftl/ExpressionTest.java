package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.WrongTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.StandardOpenOption;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpressionTest {

    private TemplateBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new Configuration().builder();
    }

    @Test
    void stringConcat() throws ParseException {
        Template template = builder.getTemplate("test", "test: ${('abcdefg' + 'hijklmnop' + 'qrstuvwxyz')?upper_case}");
        assertEquals("test: ABCDEFGHIJKLMNOPQRSTUVWXYZ", template.process(Map.of()));
    }

    @Test
    void stringConcatWithVars() throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(prefix + 'hijklmnop' + suffix)?upper_case}");
        assertEquals("test: ABCDEFGHIJKLMNOPQRSTUVWXYZ", template.process(Map.of("prefix", "abcdefg", "suffix", "qrstuvwxyz")));
    }

    @Test
    void stringConcatWithEmptyVars() throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(prefix + 'hijklmnop' + suffix)?upper_case}");
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
            "3 ≥ 1, true",
            "1 ≥ 3, false",
            "3 ≤ 1, false",
            "1 ≤ 3, true",
    })
    void numberRelation(String expression, boolean result) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(" + expression + ")?c}");
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
        Template template = builder.getTemplate("test", "test: ${(" + expression + ")?c}");
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
            "!(first < first), true",
            "!(first < second), false",
            "!(first > first), true",
            "!(second > first), false",
            "!(first <= first), false",
            "!(second >= first), false",
    })
    void negatedRelation(String expression, boolean result) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(" + expression + ")?c}");
        assertEquals("test: " + result, template.process(Map.of("first", 1, "second", 2)));
    }

    @ParameterizedTest
    @CsvSource({
            "4 == (test?ordinal), true",
            "4 != (test?ordinal), false",
            "4 <= (test?ordinal), true",
            "3 < (test?ordinal), true",
            "4 >= (test?ordinal), true",
            "5 > (test?ordinal), true",
            "4 ≤ (test?ordinal), true",
            "4 ≥ (test?ordinal), true"
    })
    void relationWithEnum(String expression, boolean result) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(" + expression + ")?c}");
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
            "true ∧ true, true",
            "true ∧ false, false",
            "false ∧ true, false",
            "false ∧ false, false",
            "true | true, true",
            "true | false, true",
            "false | true, true",
            "false | false, false",
            "true || true, true",
            "true || false, true",
            "false || true, true",
            "false || false, false",
            "true || 1, true",
            "true ∨ true, true",
            "true ∨ false, true",
            "false ∨ true, true",
            "false ∨ false, false",
            "true ^ true, false",
            "true ^ false, true",
            "false ^ true, true",
            "false ^ false, false",
            "first ^ first, false",
            "first ^ second, true",
            "second ^ second, false",
            "true ⊻ true, false",
            "true ⊻ false, true",
            "false ⊻ true, true",
            "false ⊻ false, false",
            "first ⊻ first, false",
            "first ⊻ second, true",
            "second ⊻ second, false",
            "first | first, true",
            "first | second, true",
            "second | second, false",
            "first || first, true",
            "first || second, true",
            "second || second, false",
            "first ∨ first, true",
            "first ∨ second, true",
            "second ∨ second, false",
            "first & first, true",
            "first & second, false",
            "second & second, false",
            "first && first, true",
            "first && second, false",
            "second && second, false",
            "first && first, true",
            "first && second, false",
            "second && second, false",
            "first ∧ first, true",
            "first ∧ second, false",
            "second ∧ second, false",
            "first ∧ first, true",
            "first ∧ second, false",
            "second ∧ second, false",
    })
    void junction(String expression, boolean result) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(" + expression + ")?c}");
        assertEquals("test: " + result, template.process(Map.of("first", true, "second", false)));
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
            "first ^ first, false",
            "first ^ second, true",
            "second ^ second, false",
            "first | first, true",
            "first | second, true",
            "second | second, false",
            "first || first, true",
            "first || second, true",
            "second || second, true",
            "first & first, true",
            "first & second, false",
            "second & second, false",
            "first && first, false",
            "first && second, true",
            "second && second, false",
    })
    void negatedJunction(String expression, boolean result) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(!(" + expression + "))?c}");
        assertEquals("test: " + !result, template.process(Map.of("first", false, "second", true)));
    }

    @Test
    void simpleHashLiteral() {
        Template template = builder.getTemplate("test", "${{ 'key': 42 }.key}");
        assertEquals("42", template.process(Map.of()));
    }

    @Test
    void invalidKeyInHashLiteral() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.getTemplate("test", "${{ key: 42 } }.key}"));
        assertEquals("key is not a string", exception.getMessage());
    }

    @Test
    void invalidValueInHashLiteral() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.getTemplate("test", "${{ 'key1': { 'key2' : 42 } } }.key1.key2}"));
        assertEquals("value is not a primitive", exception.getMessage());
    }

    @Test
    void simpleListLiteral() {
        Template template = builder.getTemplate("test", "${[1,2,'3',4,5<6][2]}");
        assertEquals("3", template.process(Map.of()));
    }

    @Test
    void simpleListLiteralWithoutComma() {
        Template template = builder.getTemplate("test", "${[1  2 '3'  true 3 < 4][2]}");
        assertEquals("3", template.process(Map.of()));
    }

    @Test
    void invalidDotKeyUsage() {
        Template template = builder.getTemplate("test", "${''.value}");
        assertThrows(WrongTypeException.class, () -> template.process(Map.of()));
    }

    @Test
    void dotKeyOnNull() {
        Template template = builder.getTemplate("test", "${null.value}");
        assertThrows(ProcessException.class, () -> template.process(Map.of()));
    }

    @Test
    void invalidRelationUsage() {
        Template template = builder.getTemplate("test", "${true > false}");
        assertThrows(ProcessException.class, () -> template.process(Map.of()));
    }

    @Test
    void invalidNegateUsage() {
        Template template = builder.getTemplate("test", "${-.now}");
        assertThrows(ProcessException.class, () -> template.process(Map.of()));
    }
}
