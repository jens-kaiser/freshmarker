package org.freshmarker.core;

import org.freshmarker.Configuration;
import org.freshmarker.ReductionStatus;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.core.model.TemplateBuiltIn;
import org.freshmarker.core.model.TemplateDefault;
import org.freshmarker.core.model.TemplateDotKey;
import org.freshmarker.core.model.TemplateEquality;
import org.freshmarker.core.model.TemplateExists;
import org.freshmarker.core.model.TemplateJunction;
import org.freshmarker.core.model.TemplateLengthLimitedRange;
import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateObjectVisitor;
import org.freshmarker.core.model.TemplateOperation;
import org.freshmarker.core.model.TemplateRelational;
import org.freshmarker.core.model.TemplateRightLimitedRange;
import org.freshmarker.core.model.TemplateRightUnlimitedRange;
import org.freshmarker.core.model.TemplateSlice;
import org.freshmarker.core.model.TemplateVariable;
import org.freshmarker.core.model.builtin.HookedBuiltIn;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@Nested
class ReduceExpressionTest {
    private final ReductionStatus reductionStatus = new ReductionStatus();
    private final TemplateBuilder templateBuilder = new Configuration().builder().with(SystemFeature.PARTIAL_EXPRESSION_REDUCTION);

    private static class ExpressionPrinter implements TemplateObjectVisitor<String> {

        @Override
        public String visit(HookedBuiltIn builtIn, String name, TemplateObject expression, List<TemplateObject> parameter) {
            if (parameter.isEmpty()) {
                return expression.accept(this) + "?" + name;
            }
            return expression.accept(this) + "?" + name + "(" + parameter.stream().map(p -> p.accept(this)).collect(Collectors.joining(",")) + ")";
        }

        @Override
        public String visit(TemplateBuiltIn builtIn, String name, TemplateObject expression, List<TemplateObject> parameter) {
            if (parameter.isEmpty()) {
                return expression.accept(this) + "?" + name;
            }
            return expression.accept(this) + "?" + name + "(" + parameter.stream().map(p -> p.accept(this)).collect(Collectors.joining(",")) + ")";
        }

        @Override
        public String visit(TemplateDotKey templateDotKey, TemplateObject map, String dotKey) {
            return map.accept(this) + "." + dotKey;
        }

        @Override
        public String visit(TemplateExists templateExists) {
            return templateExists.expression().accept(this) + "??";
        }

        @Override
        public String visit(TemplatePrimitive<?> primitive, String string) {
            return string;
        }

        @Override
        public String visit(TemplateDefault fallback) {
            return fallback.base().accept(this) + "!" + fallback.fallback().accept(this);
        }

        @Override
        public String visit(TemplateMarkup markup, TemplateObject content) {
            return content.accept(this);
        }

        @Override
        public String visit(TemplateVariable variable, String name) {
            return name;
        }

        @Override
        public String visit(TemplateEquality templateEquality, TemplateObject left, TemplateObject right) {
            return left.accept(this) + "==" + right.accept(this);
        }

        @Override
        public String visit(TemplateJunction templateJunction) {
            return templateJunction.left().accept(this) + " " + templateJunction.type() + " " + templateJunction.right().accept(this);
        }

        @Override
        public String visit(TemplateOperation templateOperation) {
            return templateOperation.left().accept(this) + " " + templateOperation.op() + " " + templateOperation.right().accept(this);
        }

        @Override
        public String visit(TemplateRelational templateRelational) {
            return templateRelational.left().accept(this) + " " + templateRelational.type() + " " + templateRelational.right().accept(this);
        }

        @Override
        public String visit(TemplateSlice templateSlice, TemplateObject sequence, TemplateObject range) {
            return sequence.accept(this) + "[" + range.accept(this) + "]";
        }

        @Override
        public String visit(TemplateRightLimitedRange templateRightLimitedRange, TemplateObject lower, TemplateObject upper, boolean exclusive) {
            return "(" + lower.accept(this) + (exclusive ? "..<" : "..") + upper.accept(this) + ")";
        }

        @Override
        public String visit(TemplateRightUnlimitedRange templateRightUnlimitedRange, TemplateObject lower) {
            return "(" + lower.accept(this) + "..)";
        }

        @Override
        public String visit(TemplateLengthLimitedRange templateLengthLimitedRange, TemplateObject lower, TemplateObject upper, TemplateObject count) {
            return "(" + lower.accept(this) + "..*" + count.accept(this) + ")";
        }
    }

    @ParameterizedTest
    @CsvSource({
            "${value1 + 23},65",
            "${23 + value1},65",
    })
    void operationWithConstants(String input, String expected) {
        Map<String, Object> reduceModel = Map.of("value1", 42);
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1", 42, "value2", 23)));
        assertEquals(new ReductionStatus(2, 2, 2, 3), reductionStatus);
    }

    @ParameterizedTest
    @CsvSource({
            "${value1 + value2},65,42 PLUS value2",
            "${value2 + value1},65,value2 PLUS 42"
    })
    void operation(String input, String expected, String reducedExpression) {
        Map<String, Object> reduceModel = Map.of("value1", 42);
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

        assertEquals(expected, reducedTemplate.process(Map.of("value1", 42, "value2", 23)));
        assertEquals(new ReductionStatus(1, 1, 1, 1), reductionStatus);
        assertEquals(reducedExpression, reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @ParameterizedTest
    @CsvSource({
            "${value1 > value2},yes,42 GT value2",
            "${value2 < value1},yes,value2 LT 42",
    })
    void relation(String input, String expected, String reducedExpression) {
        Map<String, Object> reduceModel = Map.of("value1", 42);
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1", 42, "value2", 23)));
        assertEquals(new ReductionStatus(1, 1, 1, 1), reductionStatus);
        assertEquals(reducedExpression, reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @ParameterizedTest
    @CsvSource({
            "${value1 > 23},yes",
            "${23 < value1},yes",
    })
    void relationWithConstant(String input, String expected) {
        Map<String, Object> reduceModel = Map.of("value1", 42);
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1", 42, "value2", 23)));
        assertEquals(new ReductionStatus(2, 2, 2, 3), reductionStatus);
    }

    @ParameterizedTest
    @CsvSource({
            "${value2 > 23},no,value2 GT 23",
            "${23 < value2},no,23 LT value2",
    })
    void relationWithVariableAndConstant(String input, String expected, String reducedExpression) {
        Map<String, Object> reduceModel = Map.of("value1", 42);
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1", 42, "value2", 23)));
        assertEquals(new ReductionStatus(2, 2, 1, 0), reductionStatus);
        assertEquals(reducedExpression, reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @ParameterizedTest
    @CsvSource({
            "${value2 & value1},no",
            "${value2 && value1},no",

            "${value1 & value2},no",
            "${value1 && value2},no",
    })
    void junctionAnd(String input, String expected) {
        Map<String, Object> reduceModel = Map.of("value1", true);
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("value1", true, "value2", false)));
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1", true, "value2", false)));
        assertEquals(new ReductionStatus(2, 2, 1, 3), reductionStatus);
        assertEquals("value2", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @ParameterizedTest
    @CsvSource({
            "${value3 == (value1 & value2)},yes",
            "${value3 == (value1 && value2)},yes",
    })
    void junctionAndWithLeftBooleanTrue(String input, String expected) {
        Map<String, Object> reduceModel = Map.of("value1", true);
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("value1", true, "value2", false, "value3", false)));
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1", true, "value2", false, "value3", false)));
        assertEquals(new ReductionStatus(2, 2, 1, 3), reductionStatus);
    }

    @ParameterizedTest
    @CsvSource({
            "${value2 & false},no",
            "${value2 && false},no",

            "${false & value2},no",
            "${false && value2},no",
    })
    void junctionAndWithConstants(String input, String expected) {
        Map<String, Object> reduceModel = Map.of("value1", true);
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("value1", true, "value2", false)));
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1", true, "value2", false)));
        assertEquals(new ReductionStatus(2, 2, 1, 0), reductionStatus);
    }

    @ParameterizedTest
    @CsvSource({
            "${value2 | value1},yes,value2",
            "${value2 || value1},yes,value2",

            "${value1 | value2},yes,value2",
            "${value1 || value2},yes,value2",
    })
    void junctionOr(String input, String expected, String reducedExpressison) {
        Map<String, Object> reduceModel = Map.of("value1", false);
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("value1", false, "value2", true)));
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1", false, "value2", true)));
        assertEquals(reducedExpressison, reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        assertEquals(new ReductionStatus(2, 2, 1, 3), reductionStatus);
    }

    @ParameterizedTest
    @CsvSource({
            "${value2 ^ value1},yes,value2 XOR false",
            "${value1 ^ value2},yes,false XOR value2",
    })
    void junctionXor(String input, String expected, String reducedExpressison) {
        Map<String, Object> reduceModel = Map.of("value1", false);
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("value1", false, "value2", true)));
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1", false, "value2", true)));
        assertEquals(reducedExpressison, reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
    }

    @Test
    void junctionXor() {
        Template template = templateBuilder.getTemplate("test", "${value2 ^ value2}");
        assertEquals("no", template.process(Map.of("value1", false, "value2", true)));
        Template reducedTemplate = template.reduce(Map.of(), reductionStatus);
        assertEquals("no", reducedTemplate.process(Map.of("value1", false, "value2", true)));
        assertEquals("value2 XOR value2", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        assertEquals(new ReductionStatus(2, 2, 1, 0), reductionStatus);
    }

    @Test
    void junctionXorWithTwoBoolean() {
        Template template = templateBuilder.getTemplate("test", "${value1 == (value2 ^ value3)}");
        Map<String, Object> model = Map.of("value1", false, "value2", true, "value3", true);
        assertEquals("yes", template.process(model));
        Map<String, Object> reduceModel = Map.of("value2", true, "value3", true);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals("yes", reducedTemplate.process(model));
        assertEquals("value1==false", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        assertEquals(new ReductionStatus(2, 2, 1, 4), reductionStatus);
    }

    @Test
    void junctionOrWithTwoBoolean() {
        Template template = templateBuilder.getTemplate("test", "${value1 == (value2 | value3)}");
        Map<String, Object> model = Map.of("value1", false, "value2", true, "value3", true);
        assertEquals("no", template.process(model));
        Map<String, Object> reduceModel = Map.of("value2", true, "value3", true);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals("no", reducedTemplate.process(model));
        assertEquals("value1==true", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        assertEquals(new ReductionStatus(2, 2, 1, 4), reductionStatus);
    }

    @Test
    void junctionAndWithTwoBoolean() {
        Template template = templateBuilder.getTemplate("test", "${value1 == (value2 & value3)}");
        Map<String, Object> model = Map.of("value1", false, "value2", true, "value3", true);
        assertEquals("no", template.process(model));
        Map<String, Object> reduceModel = Map.of("value2", true, "value3", true);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals("no", reducedTemplate.process(model));
        assertEquals("value1==true", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        assertEquals(new ReductionStatus(2, 2, 1, 4), reductionStatus);
    }

    @Test
    void defaultValueWithoutBase() {
        Template template = templateBuilder.getTemplate("test", "${value1!value2 == value3}");
        assertEquals("no", template.process(Map.of("value2", 42, "value3", 23)));
        Template reducedTemplate = template.reduce(Map.of("value2", 42), reductionStatus);
        assertEquals("no", reducedTemplate.process(Map.of("value2", 42, "value3", 23)));
        assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
        assertEquals("value1!42==value3", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @Test
    void defaultValue() {
        Template template = templateBuilder.getTemplate("test", "${value1!value2 == value3}");
        assertEquals("no", template.process(Map.of("value1", 43, "value2", 42, "value3", 23)));
        Template reducedTemplate = template.reduce(Map.of("value1", 43, "value2", 42), reductionStatus);
        assertEquals("no", reducedTemplate.process(Map.of("value1", 43, "value2", 42, "value3", 23)));
        assertEquals(new ReductionStatus(2, 2, 1, 2), reductionStatus);
        assertEquals("43==value3", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @Test
    void localeBuiltInVariable() {
        Template template = templateBuilder.withLocale(Locale.GERMANY).getTemplate("test", "${.locale}");
        assertEquals("de_DE", template.process(Map.of()));
        Template reducedTemplate = template.reduce(Map.of(), reductionStatus);
        assertEquals("de_DE", reducedTemplate.process(Map.of()));
        assertEquals(new ReductionStatus(2, 2, 2, 0), reductionStatus);
    }

    @Test
    void exists() {
        Template template = templateBuilder.getTemplate("test", "${value1??}");
        assertEquals("yes", template.process(Map.of("value1", 43)));
        Template reducedTemplate = template.reduce(Map.of("value1", 43), reductionStatus);
        assertEquals("yes", reducedTemplate.process(Map.of()));
        assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
    }

    @Test
    void existsNot() {
        Template template = templateBuilder.getTemplate("test", "${value1??}");
        assertEquals("yes", template.process(Map.of("value1", 43)));
        Template reducedTemplate = template.reduce(Map.of(), reductionStatus);
        assertEquals("yes", reducedTemplate.process(Map.of("value1", 43)));
        assertEquals(new ReductionStatus(2, 2, 1, 0), reductionStatus);
        assertEquals("value1??", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @Test
    void dotKey() {
        Template template = templateBuilder.getTemplate("test", "${value1.key == value2}");
        assertEquals("no", template.process(Map.of("value1", Map.of("key", 23), "value2", 42)));
        Template reducedTemplate = template.reduce(Map.of("value1", Map.of("key", 23)), reductionStatus);
        assertEquals("no", reducedTemplate.process(Map.of("value1", Map.of("key", 23), "value2", 42)));
        assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
        assertEquals("23==value2", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @Test
    void dotKeyWithoutAttribute() {
        Template template = templateBuilder.getTemplate("test", "${value1.key == value2}");
        assertEquals("no", template.process(Map.of("value1", Map.of("key", 23), "value2", 42)));
        Template reducedTemplate = template.reduce(Map.of("value1", Map.of()), reductionStatus);
        assertEquals("no", reducedTemplate.process(Map.of("value1", Map.of("key", 23), "value2", 42)));
        assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
        assertEquals("value1.key==value2", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @Test
    void not() {
        Template template = templateBuilder.getTemplate("test", "${value1 == !value2}");
        assertEquals("yes", template.process(Map.of("value1", false, "value2", true)));
        Template reducedTemplate = template.reduce(Map.of("value2", true), reductionStatus);
        assertEquals("yes", reducedTemplate.process(Map.of("value1", false, "value2", true)));
        assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
        assertEquals("value1==false", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @Test
    void builtIn() {
        Template template = templateBuilder.getTemplate("test", "${value1?upper_case ~ value2?upper_case}");
        assertEquals("JENS KAISER", template.process(Map.of("value1" , "Jens", "value2", "Kaiser")));
        Template reducedTemplate = template.reduce(Map.of("value2", "Kaiser"), reductionStatus);
        assertEquals("JENS KAISER", reducedTemplate.process(Map.of("value1", "Jens", "value2", "Kaiser")));
        assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
        assertEquals("value1?upper_case CONCAT 'KAISER'", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }

    @Nested
    class Hooked {
        @Test
        void builtIn() {
            Template template = templateBuilder.getTemplate("test", "${value1?upper_case ~ value2?upper_case}");
            assertEquals("JENS KAISER", template.process(Map.of("value1" , "Jens", "value2", "Kaiser")));
            Template reducedTemplate = template.reduce(Map.of("value2", "Kaiser"), reductionStatus);
            assertEquals("JENS KAISER", reducedTemplate.process(Map.of("value1", "Jens", "value2", "Kaiser")));
            assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
            assertEquals("value1?upper_case CONCAT 'KAISER'", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }

        @Test
        void builtInWithParameter() {
            Template template = templateBuilder.getTemplate("test", "${value1?max(value2)}");
            assertEquals("42", template.process(Map.of("value1" , 23, "value2", 42)));
            Template reducedTemplate = template.reduce(Map.of("value2", 42), reductionStatus);
            assertEquals("42", reducedTemplate.process(Map.of("value1" , 23, "value2", 42)));
            assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
            assertEquals("value1?max(42)", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }

        @Test
        void builtInWithUnknownParameter() {
            Template template = templateBuilder.getTemplate("test", "${value1?max(value2)}");
            assertEquals("42", template.process(Map.of("value1" , 23, "value2", 42)));
            Template reducedTemplate = template.reduce(Map.of("value1", 23), reductionStatus);
            assertEquals("42", reducedTemplate.process(Map.of("value1" , 23, "value2", 42)));
            assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
            assertEquals("23?max(value2)", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }
    }

    @Nested
    class Unhooked {
        @Test
        void builtInWithParameter() {
            Template template = templateBuilder.getTemplate("test", "${value1?supports(value2)}");
            assertEquals("yes", template.process(Map.of("value1" , Year.of(2025), "value2", "YEARS")));
            Template reducedTemplate = template.reduce(Map.of("value2", "YEARS"), reductionStatus);
            assertEquals("yes", reducedTemplate.process(Map.of("value1" , Year.of(2025), "value2", "YEARS")));
            assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
            assertEquals("value1?supports('YEARS')", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }

        @Test
        void builtInWithUnknownParameter() {
            Template template = templateBuilder.getTemplate("test", "${value1?supports(value2)}");
            assertEquals("yes", template.process(Map.of("value1" , Year.of(2025), "value2", "YEARS")));
            Template reducedTemplate = template.reduce(Map.of("value1", Year.of(2025)), reductionStatus);
            assertEquals("yes", reducedTemplate.process(Map.of("value1" , Year.of(2025), "value2", "YEARS")));
            assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
            assertEquals("2025?supports(value2)", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "${(value1..value2)?join(' ')},11,19,11,,11 12 13 14 15 16 17 18 19,(11..value2)?join(' ')",
            "${(value1..value2)?join(' ')},11,19,,19,11 12 13 14 15 16 17 18 19,(value1..19)?join(' ')",
            "${(value1..<value2)?join(' ')},11,20,11,,11 12 13 14 15 16 17 18 19,(11..<value2)?join(' ')",
            "${(value1..<value2)?join(' ')},11,20,,20,11 12 13 14 15 16 17 18 19,(value1..<20)?join(' ')",
            "${(value1..*value2)?join(' ')},11,9,11,,11 12 13 14 15 16 17 18 19,(11..*value2)?join(' ')",
            "${(value1..*value2)?join(' ')},11,9,,9,11 12 13 14 15 16 17 18 19,(value1..*9)?join(' ')",
    })
    void range(String input, int lower, int upper, Integer reduceLower, Integer reduceUpper, String expected, String reduced) {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("value1" , lower, "value2", upper)));
        Map<String, Object> reduceModel = new HashMap<>();
        reduceModel.put("value1", reduceLower);
        reduceModel.put("value2", reduceUpper);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertEquals(expected, reducedTemplate.process(Map.of("value1" , lower, "value2", upper)));
        assertEquals(new ReductionStatus(2, 2, 1, 1), reductionStatus);
        assertEquals(reduced, reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
    }
}
