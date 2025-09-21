package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.WrongTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
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
    void constantFragments() throws ParseException {
        Template template = builder.getTemplate("test", "test: dies ist einfach nur ein text");
        assertEquals("test: dies ist einfach nur ein text", template.process(Map.of()));
    }

    @Test
    void signedValues() throws ParseException {
        Template template = builder.getTemplate("test", "test: ${-42} ${-a} ${+42} ${+a}");
        assertEquals("test: -42 -42 42 42", template.process(Map.of("a", 42)));
    }

    @Nested
    class ConcatWithPlusOperator {
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
    }

    @Nested
    class ConcatWithTildeOperator {
        @Test
        void stringConcat() throws ParseException {
            Template template = builder.getTemplate("test", "test: ${('abcdefg' ~ 'hijklmnop' ~ 'qrstuvwxyz')?upper_case}");
            assertEquals("test: ABCDEFG HIJKLMNOP QRSTUVWXYZ", template.process(Map.of()));
        }

        @Test
        void stringConcatWithVars() throws ParseException {
            Template template = builder.getTemplate("test", "test: ${(prefix ~ 'hijklmnop' ~ suffix)?upper_case}");
            assertEquals("test: ABCDEFG HIJKLMNOP QRSTUVWXYZ", template.process(Map.of("prefix", "abcdefg", "suffix", "qrstuvwxyz")));
        }

        @Test
        void stringConcatWithEmptyVars() throws ParseException {
            Template template = builder.getTemplate("test", "test: ${(prefix ~ 'hijklmnop' ~ suffix)?upper_case}");
            assertEquals("test: HIJKLMNOP", template.process(Map.of("prefix", "", "suffix", "")));
        }
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
            "1 == 1, true",
            "1 == 2, false",
            "1 = 1, true",
            "1 = 2, false",
            "1 != 1, false",
            "1 != 2, true",
    })
    void numberRelationAndEquality(String expression, boolean result) throws ParseException {
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
            "!(second <=> first), -1",
    })
    void negatedRelation(String expression, String result) throws ParseException {
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
            "first & false, false",
            "true && true, true",
            "true && false, false",
            "false && true, false",
            "false && false, false",
            "false && 1, false",
            "first && false, false",
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
            "first || true, true",
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

    @ParameterizedTest
    @CsvSource({
            "${(1..10)[5]}, 6",
            "${(-1..-10)[5]}, -6"
    })
    void hash(String input, String expected) {
        Template template = builder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of()));
    }


    @Test
    void outOfBoundsIndex() {
        Template template = builder.getTemplate("test", "${seq[5]}");
        Map<String, Object> model = Map.of("seq", List.of());
        ProcessException exception = assertThrows(ProcessException.class, () -> template.process(model));
        assertEquals("Index 5 out of bounds for length 0 at test:1:1 '${seq[5]}'", exception.getMessage());
    }

    @Test
    void unsupportedHash() {
        Template template = builder.getTemplate("test", "${42[1]}");
        Map<String, Object> model = Map.of();
        ProcessException exception = assertThrows(ProcessException.class, () -> template.process(model));
        assertEquals("unsupported type: class java.lang.Integer at test:1:1 '${42[1]}'", exception.getMessage());
    }

    @Test
    void invalidDotKeyUsage() {
        Template template = builder.getTemplate("test", "${''.value}");
        Map<String, Object> model = Map.of();
        assertThrows(WrongTypeException.class, () -> template.process(model));
    }

    @Test
    void invalidEquality() {
        Template template = builder.getTemplate("test", "${1..2 == 1..2}");
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "${null.value}",
            "${truth > false}",
            "${-.now}",
            "${list != list}"
    })
    void invalidUsages(String input) {
        Template template = builder.getTemplate("test", input);
        Map<String, Object> model = Map.of("truth", true);
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void listOperation() {
        Template template = builder.getTemplate("test", "${([0,1,2,3,4] + [5,6,7,8,9])?join}");
        assertEquals("0, 1, 2, 3, 4, 5, 6, 7, 8, 9", template.process(Map.of()));
    }

    @Test
    void invalidRelation() {
        ParsingException exception = assertThrows(ParsingException.class, () -> builder.getTemplate("test", "${true < false}"));
        assertEquals("unsupported relation: LT at test:1:3 'true < false'", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "${42 <=> 0},1",
            "${42 <=> 42},0",
            "${42 <=> 128},-1",
            "${'Tom' <=> 'Jerry'},1",
            "${'Jens' <=> 'Jens'},0",
            "${'Fix' <=> 'Foxy'},-1",
            "${yesterday <=> today},-1",
            "${today <=> today},0",
            "${today <=> yesterday},1",
            "${one_day <=> one_week},-1",
            "${one_day <=> one_day},0",
            "${one_week <=> one_day},1"
    })
    void spaceshipOperator(String input, String expected) {
        Template template = builder.getTemplate("spaceship", input);
        Map<String, Object> dataModel = Map.of(
                "today", LocalDate.now(),
                "yesterday", LocalDate.now().minusDays(1),
                "one_day", Period.ofDays(1),
                "one_week", Period.ofDays(7));
        assertEquals(expected, template.process(dataModel));
    }

    @Nested
    class Elvis {
        @Test
        void simple() {
            Template template = builder.getTemplate("test", "${value?:42}");
            assertEquals("42", template.process(Map.of()));
        }

        @Test
        void withBuiltIn() {
            Template template = builder.getTemplate("test", "${'value'?upper_case?:42}");
            assertEquals("VALUE", template.process(Map.of()));
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${sequence?join(',', ' or ')};test: 1,2,3,4 or 5",
            "test: ${sequence?join(':')};test: 1:2:3:4:5",
            "test: ${sequence?join};test: 1, 2, 3, 4, 5",
    }, delimiterString = ";")
    void join(String input, String expected) throws ParseException {
        Template template = builder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("sequence", List.of(1,2,3,4,5))));
    }
}
