package org.freshmarker.core;

import org.freshmarker.Configuration;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.ReductionStatus;
import org.freshmarker.Template;
import org.freshmarker.core.model.TemplateDefault;
import org.freshmarker.core.model.TemplateDotKey;
import org.freshmarker.core.model.TemplateEquality;
import org.freshmarker.core.model.TemplateExists;
import org.freshmarker.core.model.TemplateJunction;
import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateObjectVisitor;
import org.freshmarker.core.model.TemplateOperation;
import org.freshmarker.core.model.TemplateRelational;
import org.freshmarker.core.model.TemplateVariable;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReduceTemplateTest {
    private final ReductionStatus reductionStatus = new ReductionStatus();
    private TemplateBuilder templateBuilder = new Configuration().builder();

    @Nested
    class ExpressionsWithoutPartialReduction {
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
            assertEquals(new ReductionStatus(2,2,2,0), reductionStatus);
        }
    }

    @Nested
    class Expressions {
        private final TemplateBuilder templateBuilder = ReduceTemplateTest.this.templateBuilder.with(SystemFeature.PARTIAL_EXPRESSION_REDUCTION);

        private static class ExpressionPrinter implements TemplateObjectVisitor<String> {
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
                return templateJunction.left().accept(this) + " " +  templateJunction.type() + " " + templateJunction.right().accept(this);
            }

            @Override
            public String visit(TemplateOperation templateOperation) {
                return templateOperation.left().accept(this) + " " +  templateOperation.op() + " " + templateOperation.right().accept(this);
            }

            @Override
            public String visit(TemplateRelational templateRelational) {
                return templateRelational.left().accept(this) + " " + templateRelational.type() + " " + templateRelational.right().accept(this);
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
            assertEquals(new ReductionStatus(2,2,2,3), reductionStatus);
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
            assertEquals(new ReductionStatus(1,1,1,1), reductionStatus);
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
            assertEquals(new ReductionStatus(1,1,1,1), reductionStatus);
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
            assertEquals(new ReductionStatus(2,2,2,3), reductionStatus);
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
            assertEquals(new ReductionStatus(2,2,1,0), reductionStatus);
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
            assertEquals(new ReductionStatus(2,2,1,3), reductionStatus);
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
            assertEquals(new ReductionStatus(2,2,1,3), reductionStatus);
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
            assertEquals(new ReductionStatus(2,2,1,0), reductionStatus);
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
            assertEquals(new ReductionStatus(2,2,1,3), reductionStatus);
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
            assertEquals(new ReductionStatus(2,2,1,1), reductionStatus);
        }

        @Test
        void junctionXor() {
            Template template = templateBuilder.getTemplate("test", "${value2 ^ value2}");
            assertEquals("no", template.process(Map.of("value1", false, "value2", true)));
            Template reducedTemplate = template.reduce(Map.of(), reductionStatus);
            assertEquals("no", reducedTemplate.process(Map.of("value1", false, "value2", true)));
            assertEquals("value2 XOR value2", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
            assertEquals(new ReductionStatus(2,2,1,0), reductionStatus);
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
            assertEquals(new ReductionStatus(2,2,1,4), reductionStatus);
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
            assertEquals(new ReductionStatus(2,2,1,4), reductionStatus);
        }

        @Test
        void defaultValueWithoutBase() {
            Template template = templateBuilder.getTemplate("test", "${value1!value2 == value3}");
            assertEquals("no", template.process(Map.of("value2", 42, "value3", 23)));
            Template reducedTemplate = template.reduce(Map.of("value2", 42), reductionStatus);
            assertEquals("no", reducedTemplate.process(Map.of("value2", 42, "value3", 23)));
            assertEquals(new ReductionStatus(2,2,1,1), reductionStatus);
            assertEquals("value1!42==value3", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }

        @Test
        void defaultValue() {
            Template template = templateBuilder.getTemplate("test", "${value1!value2 == value3}");
            assertEquals("no", template.process(Map.of("value1", 43, "value2", 42, "value3", 23)));
            Template reducedTemplate = template.reduce(Map.of("value1", 43, "value2", 42), reductionStatus);
            assertEquals("no", reducedTemplate.process(Map.of("value1", 43, "value2", 42, "value3", 23)));
            assertEquals(new ReductionStatus(2,2,1,2), reductionStatus);
            assertEquals("43==value3", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }

        @Test
        void localeBuiltInVariable() {
            Template template = templateBuilder.withLocale(Locale.GERMANY).getTemplate("test", "${.locale}");
            assertEquals("de_DE", template.process(Map.of()));
            Template reducedTemplate = template.reduce(Map.of(), reductionStatus);
            assertEquals("de_DE", reducedTemplate.process(Map.of()));
            assertEquals(new ReductionStatus(2,2,2,0), reductionStatus);
        }

        @Test
        void exists() {
            Template template = templateBuilder.getTemplate("test", "${value1??}");
            assertEquals("yes", template.process(Map.of("value1", 43)));
            Template reducedTemplate = template.reduce(Map.of("value1", 43), reductionStatus);
            assertEquals("yes", reducedTemplate.process(Map.of()));
            assertEquals(new ReductionStatus(2,2,1,1), reductionStatus);
        }

        @Test
        void existsNot() {
            Template template = templateBuilder.getTemplate("test", "${value1??}");
            assertEquals("yes", template.process(Map.of("value1", 43)));
            Template reducedTemplate = template.reduce(Map.of(), reductionStatus);
            assertEquals("yes", reducedTemplate.process(Map.of("value1", 43)));
            assertEquals(new ReductionStatus(2,2,1,0), reductionStatus);
            assertEquals("value1??", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }

        @Test
        void dotKey() {
            Template template = templateBuilder.getTemplate("test", "${value1.key == value2}");
            assertEquals("no", template.process(Map.of("value1", Map.of("key", 23), "value2", 42)));
            Template reducedTemplate = template.reduce(Map.of("value1", Map.of("key", 23)), reductionStatus);
            assertEquals("no", reducedTemplate.process(Map.of("value1", Map.of("key", 23), "value2", 42)));
            assertEquals(new ReductionStatus(2,2,1,1), reductionStatus);
            assertEquals("23==value2", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }

        @Test
        void dotKeyWithoutAttribute() {
            Template template = templateBuilder.getTemplate("test", "${value1.key == value2}");
            assertEquals("no", template.process(Map.of("value1", Map.of("key", 23), "value2", 42)));
            Template reducedTemplate = template.reduce(Map.of("value1", Map.of()), reductionStatus);
            assertEquals("no", reducedTemplate.process(Map.of("value1", Map.of("key", 23), "value2", 42)));
            assertEquals(new ReductionStatus(2,2,1,1), reductionStatus);
            assertEquals("value1.key==value2", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }

        @Test
        void not() {
            Template template = templateBuilder.getTemplate("test", "${value1 == !value2}");
            assertEquals("yes", template.process(Map.of("value1", false,  "value2", true)));
            Template reducedTemplate = template.reduce(Map.of("value2", true), reductionStatus);
            assertEquals("yes", reducedTemplate.process(Map.of("value1", false,  "value2", true)));
            assertEquals(new ReductionStatus(2,2,1,1), reductionStatus);
            assertEquals("value1==false", reductionStatus.expressions().getLast().accept(new ExpressionPrinter()));
        }
    }

    @Nested
    class UnfoldListDirectiveWithMergeConstantFragments {
        @BeforeEach
        void setUp() {
            templateBuilder = templateBuilder.with(ReductionFeature.UNROLL_LIST);
        }

        @Test
        void reduceListWithHiddenValue() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4));
            String input = "<#list seq as company>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
            assertEquals("1 2 3 4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 13, 9), reductionStatus);
        }

        @Test
        void reduceList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4));
            String input = "<#list seq as s>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 13, 9), reductionStatus);
        }

        @Test
        void reduceListWithFilterAndOffset() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
            String input = "<#list seq as s filter s % 2 == 0 offset 1 limit 4>${s} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("4 6 8 10 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))));
            assertEquals(new ReductionStatus(5, 13, 9), reductionStatus);
        }

        @Test
        void reduceHashList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", Map.of("1", 2, "2", 4));
            String input = "<#list seq as k, v>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", Map.of("1", 2, "2", 4))));
            assertEquals(new ReductionStatus(5, 7, 5), reductionStatus);
        }

        @Test
        void reduceListWithLoopVariableAndVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = """
                <#var name=firstname>
                <#list seq as s with l>
                ${l?counter}. ${company}/${s} ${name}
                </#list>""";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("""
                1. schegge.de/1 jens
                2. schegge.de/2 jens
                3. schegge.de/3 jens
                4. schegge.de/4 jens
                """, reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(13, 12, 5), reductionStatus);
        }

        @Test
        void reduceListWithItem() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#list seq as s>${company}/${s} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 schegge.de/2 schegge.de/3 schegge.de/4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(7, 7, 3), reductionStatus);
        }

        @Test
        void reduceWithConstantVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#var name='Jens'><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(10, 9, 5), reductionStatus);
        }

        @Test
        void reduceListWithLoopVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#list seq as s with l>${l?counter} ${company}/${s} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("1 schegge.de/1 2 schegge.de/2 3 schegge.de/3 4 schegge.de/4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(9, 9, 3), reductionStatus);
        }

        @Test
        void reduceStackedListWithLoopVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = """
                <#var name=0>
                <#list seq1 as s with l>
                <#list seq2 as s with l>
                <#set name=name+1>
                ${l?counter}. ${company}/${s} ${name}
                </#list>
                </#list>""";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("""
                1. schegge.de/3 1
                2. schegge.de/4 2
                1. schegge.de/3 3
                2. schegge.de/4 4
                """, reducedTemplate.process(Map.of("seq1", List.of(1, 2), "seq2", List.of(3, 4))));
            assertEquals(new ReductionStatus(15, 14, 5), reductionStatus);
        }

        @Test
        void reduceNotListList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#list seq as s>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 5, 3), reductionStatus);
        }
    }

    @Nested
    class UnrollListDirective {
        @BeforeEach
        void setUp() {
            templateBuilder = templateBuilder.with(ReductionFeature.UNROLL_LIST).with(ReductionFeature.MERGE_CONSTANT_FRAGMENTS);
        }

        @Test
        void reduceListWithHiddenValue() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4));
            String input = "<#list seq as company>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("1 2 3 4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 5, 9), reductionStatus);
        }

        @Test
        void reduceList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4));
            String input = "<#list seq as s>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 5, 9), reductionStatus);
        }

        @Test
        void reduceListWithFilterAndOffset() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
            String input = "<#list seq as s filter s % 2 == 0 offset 1 limit 4>${s} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("4 6 8 10 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))));
            assertEquals(new ReductionStatus(5, 5, 9), reductionStatus);
        }

        @Test
        void reduceHashList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", Map.of("1", 2, "2", 4));
            String input = "<#list seq as k, v>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", Map.of("1", 2, "2", 4))));
            assertEquals(new ReductionStatus(5, 3, 5), reductionStatus);
        }

        @Test
        void reduceLongHashList() {
            Map<String, Integer> map = IntStream.range(0, 10).boxed().collect(Collectors.toMap(String::valueOf, e -> e));
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", map);
            String input = "<#list seq as k, v>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", Map.of(1, 2, 2, 4))));
            assertEquals(new ReductionStatus(5, 3, 3), reductionStatus);
        }

        @Test
        void reduceListWithLoopVariableAndVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = """
                <#var name=firstname>
                <#list seq as s with l>
                ${l?counter}. ${company}/${s} ${name}
                </#list>""";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("""
                1. schegge.de/1 jens
                2. schegge.de/2 jens
                3. schegge.de/3 jens
                4. schegge.de/4 jens
                """, reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(13, 10, 5), reductionStatus);
        }

        @Test
        void reduceListWithItem() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#list seq as s>${company}/${s} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 schegge.de/2 schegge.de/3 schegge.de/4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(7, 6, 3), reductionStatus);
        }

        @Test
        void reduceWithConstantVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#var name='Jens'><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(10, 8, 5), reductionStatus);
        }

        @Test
        void reduceListWithLoopVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#list seq as s with l>${l?counter} ${company}/${s} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("1 schegge.de/1 2 schegge.de/2 3 schegge.de/3 4 schegge.de/4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(9, 7, 3), reductionStatus);
        }

        @Test
        void reduceStackedListWithLoopVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = """
                <#var name=0>
                <#list seq1 as s with l>
                <#list seq2 as s with l>
                <#set name=name+1>
                ${l?counter}. ${company}/${s} ${name}
                </#list>
                </#list>""";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("""
                1. schegge.de/3 1
                2. schegge.de/4 2
                1. schegge.de/3 3
                2. schegge.de/4 4
                """, reducedTemplate.process(Map.of("seq1", List.of(1, 2), "seq2", List.of(3, 4))));
            assertEquals(new ReductionStatus(15, 12, 5), reductionStatus);
        }

        @Test
        void reduceNotListList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#list seq as s>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 3, 3), reductionStatus);
        }

        @Test
        void notUnrollList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4));
            String input = "<#list seq as s>${company} </#list>";
            Template template = templateBuilder.with(ReductionFeature.UNROLL_LIST, 2).getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 3, 3), reductionStatus);
        }
    }

    @Nested
    class WithMergeConstantFragments {
        @BeforeEach
        void setUp() {
            templateBuilder = templateBuilder.with(ReductionFeature.MERGE_CONSTANT_FRAGMENTS);
        }

        @Test
        void reduceBlock() {
            Map<String, Object> model = Map.of("company", "schegge.de");
            Template template = templateBuilder.getTemplate("test", "${company}: ${name}").reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de: Jens Kaiser", template.process(Map.of("name", "Jens Kaiser")));
            assertEquals(new ReductionStatus(4, 3, 2), reductionStatus);

        }

        @Test
        void reduceBlockWithDefault() {
            Map<String, Object> model = Map.of("company", "schegge.de");
            Template template = templateBuilder.getTemplate("test", "${company} ${name!}").reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de Jens Kaiser", template.process(Map.of("name", "Jens Kaiser")));
            assertEquals(new ReductionStatus(4, 3, 2), reductionStatus);
        }

        @Test
        void reduceOutputFormat() {
            Map<String, Object> model = Map.of("company", "schegge.de");
            Template template = templateBuilder.getTemplate("test", "<#outputformat 'HTML'>${company}: ${name}</#outputformat>").reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de: Jens Kaiser", template.process(Map.of("name", "Jens Kaiser")));
            assertEquals(new ReductionStatus(6, 5, 4), reductionStatus);
        }

        @Test
        void reduceMultipleIf() {
            Map<String, Object> model = Map.of("company", "schegge.de", "flag", 3);
            Template template = templateBuilder.getTemplate("test", """
                <#if flag == 1>
                ${company}1
                <#elseif flag == 2>
                ${company}2
                <#elseif flag == 3>
                ${company}3
                <#else>
                ${name}
                </#if>
                """).reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de3\n", template.process(Map.of("name", "Jens Kaiser", "flag", true)));
            assertEquals(new ReductionStatus(17, 2, 4), reductionStatus);
        }

        @Test
        void reduceIfElseWithException() {
            Map<String, Object> model = Map.of("company", "schegge.de", "flag", 3);
            Template template = templateBuilder.getTemplate("test", """
                <#if flag == 1>
                ${company} 1
                <#elseif galf == 2>
                ${company} 2
                </#if>
                """).reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de 2\n", template.process(Map.of("flag", 3, "galf", 2)));
            assertEquals(new ReductionStatus(11, 7, 7), reductionStatus);
        }

        @Test
        void reduceWithConstantVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#var name='Jens'><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(10, 8, 5), reductionStatus);
        }

        @Test
        void reduceWithDynamicVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = "<#var name=firstname><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(10, 8, 5), reductionStatus);
        }

        @Test
        void reduceWithDynamicVariableSet() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = "<#var name='Jens'><#set name=firstname><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(11, 9, 6), reductionStatus);
        }

        @Test
        void reduceWithEmptyVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#var name=firstname><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS",
                    reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4), "firstname", "jens")));
            assertEquals(new ReductionStatus(10, 8, 4), reductionStatus);
        }
    }

    @Nested
    class ReduceIfDirective {
        @Test
        void reduceIf() {
            Map<String, Object> model = Map.of("company", "schegge.de", "flag", true);
            Template template = templateBuilder.getTemplate("test", "<#if flag>${company}<#else>${name}</#if>").reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de", template.process(Map.of("name", "Jens Kaiser", "flag", true)));
            assertEquals(new ReductionStatus(5, 2, 3), reductionStatus);
        }

        @Test
        void reduceNotIf() {
            Map<String, Object> model = Map.of("company", "schegge.de");
            Template template = templateBuilder.getTemplate("test", "<#if flag>${company}<#else>${name}</#if>").reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de", template.process(Map.of("name", "Jens Kaiser", "flag", true)));
            assertEquals(new ReductionStatus(5, 5, 3), reductionStatus);
        }

        @Test
        void reduceIfWithExists() {
            Map<String, Object> model = Map.of("company", "schegge.de");
            Template template = templateBuilder.getTemplate("test", "<#if name??>${company}<#else>${name}</#if>").reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de", template.process(Map.of("name", "Jens Kaiser")));
            assertEquals(new ReductionStatus(5, 5, 3), reductionStatus);
        }

        @Test
        void reduceMultipleIf() {
            Map<String, Object> model = Map.of("company", "schegge.de", "flag", 3);
            Template template = templateBuilder.getTemplate("test", """
                <#if flag == 1>
                ${company}1
                <#elseif flag == 2>
                ${company}2
                <#elseif flag == 3>
                ${company}3
                <#else>
                ${name}
                </#if>
                """).reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de3\n", template.process(Map.of("name", "Jens Kaiser", "flag", true)));
            assertEquals(new ReductionStatus(17, 3, 4), reductionStatus);
        }

        @Test
        void reduceIfElseWithException() {
            Map<String, Object> model = Map.of("company", "schegge.de", "flag", 3);
            Template template = templateBuilder.getTemplate("test", """
                <#if flag == 1>
                ${company} 1
                <#elseif galf == 2>
                ${company} 2
                </#if>
                """).reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("schegge.de 2\n", template.process(Map.of("flag", 3, "galf", 2)));
            assertEquals(new ReductionStatus(11, 11, 7), reductionStatus);
        }

        @Test
        void reduceElse() {
            Map<String, Object> model = Map.of("company", "schegge.de", "flag", false);
            Template template = templateBuilder.getTemplate("test", "<#if flag>${company}<#else>${name}</#if>").reduce(model, reductionStatus);
            assertNotNull(template);
            assertEquals("Jens Kaiser", template.process(Map.of("name", "Jens Kaiser", "flag", true)));
            assertEquals(new ReductionStatus(5, 2, 1), reductionStatus);
        }
    }

    @Nested
    class ReduceSwitchDirective {
        @ParameterizedTest
        @CsvSource({
                "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>,1,schegge.de,3",
                "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>,2,Jens Kaiser,1",
                "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>,3,three,1",
        })
        void reduceSwitchCase(String input, int flag, String expected, int replaced) {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "flag", flag);
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals(expected, reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 2)));
            assertEquals(new ReductionStatus(9, 2, replaced), reductionStatus);
        }

        @Test
        void reduceNotSwitch() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("Jens Kaiser", reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 2)));
            assertEquals(new ReductionStatus(9, 9, 4), reductionStatus);
        }

        @Test
        void reduceSwitchDefault() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "flag", 4);
            String input = "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("default", reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 2)));
            assertEquals(new ReductionStatus(9, 2, 1), reductionStatus);
        }

        @Test
        void reduceSwitchWithoutCaseReduction() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("Jens Kaiser", reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 2)));
            assertEquals(new ReductionStatus(9, 9, 4), reductionStatus);
        }

        @Test
        void reduceMapSwitch() {
            Template template = templateBuilder.with(SwitchDirectiveFeature.OPTIMIZE_CONSTANT_SWITCH)
                    .getTemplate("test", "<#switch flag><#case 1>1<#case 2>2</#switch>");
            Template reducedTemplate = template.reduce(Map.of("flag", 1), reductionStatus);

            assertEquals("1", reducedTemplate.process(Map.of()));
            assertEquals(new ReductionStatus(5, 2, 1), reductionStatus);
        }

        @Test
        void reduceListSwitch() {
            Template template = templateBuilder
                    .getTemplate("test", "<#switch flag><#case 1>1<#case 2>2</#switch>");
            Template reducedTemplate = template.reduce(Map.of("flag", 1), reductionStatus);

            assertEquals("1", reducedTemplate.process(Map.of()));
            assertEquals(new ReductionStatus(7, 2, 1), reductionStatus);
        }
    }

    @Nested
    class ReduceListDirective {
        @Test
        void reduceList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4));
            String input = "<#list seq as s>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 5, 3), reductionStatus);
        }

        @Test
        void reduceNotListList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#list seq as s>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 5, 3), reductionStatus);
        }

        @Test
        void reduceListWithHiddenValue() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4));
            String input = "<#list seq as company>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("1 2 3 4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(5, 5, 2), reductionStatus);
        }

        @Test
        void reduceListWithItem() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#list seq as s>${company}/${s} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 schegge.de/2 schegge.de/3 schegge.de/4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(7, 7, 3), reductionStatus);
        }

        @Test
        void reduceListWithLoopVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#list seq as s with l>${l?counter} ${company}/${s} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("1 schegge.de/1 2 schegge.de/2 3 schegge.de/3 4 schegge.de/4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(9, 9, 3), reductionStatus);
        }

        @Test
        void reduceListWithLoopVariableAndVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = """
                <#var name=firstname>
                <#list seq as s with l>
                ${l?counter}. ${company}/${s} ${name}
                </#list>""";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("""
                1. schegge.de/1 jens
                2. schegge.de/2 jens
                3. schegge.de/3 jens
                4. schegge.de/4 jens
                """, reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(13, 12, 5), reductionStatus);
        }

        @Test
        void reduceStackedListWithLoopVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = """
                <#var name=0>
                <#list seq1 as s with l>
                <#list seq2 as s with l>
                <#set name=name+1>
                ${l?counter}. ${company}/${s} ${name}
                </#list>
                </#list>""";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
assertEquals("""
                1. schegge.de/3 1
                2. schegge.de/4 2
                1. schegge.de/3 3
                2. schegge.de/4 4
                """, reducedTemplate.process(Map.of("seq1", List.of(1, 2), "seq2", List.of(3, 4))));
            assertEquals(new ReductionStatus(15, 14, 5), reductionStatus);

        }

        @Test
        void reduceHashList() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", Map.of(1, 2, 2, 4));
            String input = "<#list seq as k, v>${company} </#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", Map.of(1, 2, 2, 4))));
            assertEquals(new ReductionStatus(5, 5, 3), reductionStatus);
        }
    }

    @Nested
    class ReduceVariable {
        @Test
        void reduceWithConstantVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#var name='Jens'><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(10, 9, 5), reductionStatus);
        }

        @Test
        void reduceWithDynamicVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = "<#var name=firstname><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(10, 9, 5), reductionStatus);

        }

        @Test
        void reduceWithDynamicVariableSet() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
            String input = "<#var name='Jens'><#set name=firstname><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
            assertEquals(new ReductionStatus(11, 10, 6), reductionStatus);
        }

        @Test
        void reduceWithEmptyVariable() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            String input = "<#var name=firstname><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(reduceModel, reductionStatus);

            assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS",
                    reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4), "firstname", "jens")));
            assertEquals(new ReductionStatus(10, 9, 4), reductionStatus);
        }

        @Test
        void reduceWithWrongType() {
            Map<String, Object> reduceModel = Map.of("company", "schegge.de");
            Template template = templateBuilder.getTemplate("test", "${1 + company}");
            assertThrows(ReduceException.class, () -> template.reduce(reduceModel, reductionStatus));
        }

        @Test
        void reduceWithNullVariable() {
            String input = "<#var name=value>${name}";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(Map.of(), reductionStatus);

            assertEquals("Jens", reducedTemplate.process(Map.of("value", "Jens")));
            assertEquals(new ReductionStatus(4, 3, 2), reductionStatus);
        }

        @Test
        void reduceWithInvalidVariableDefinition() {
            String input = "<#var name=value><#var name=value>${name}";
            Template template = templateBuilder.getTemplate("test", input);
            Template reducedTemplate = template.reduce(Map.of("value", "Jens"), reductionStatus);

            Map<String, Object> dataModel = Map.of();
            ProcessException exception = assertThrows(ProcessException.class, () -> reducedTemplate.process(dataModel));
            assertEquals("variable name must not exist at test:1:18 '<#var name=value>'", exception.getMessage());
            assertEquals(new ReductionStatus(5, 4, 4), reductionStatus);
        }
    }

    @Test
    void reduceBlock() {
        Map<String, Object> model = Map.of("company", "schegge.de");
        Template template = templateBuilder.getTemplate("test", "${company}: ${name}").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de: Jens Kaiser", template.process(Map.of("name", "Jens Kaiser")));
        assertEquals(new ReductionStatus(4, 4, 2), reductionStatus);
    }

    @Test
    void reduceBlockWithDefault() {
        Map<String, Object> model = Map.of("company", "schegge.de");
        Template template = templateBuilder.getTemplate("test", "${company} ${name!}").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de Jens Kaiser", template.process(Map.of("name", "Jens Kaiser")));
        assertEquals(new ReductionStatus(4, 4, 2), reductionStatus);
    }

    @Test
    void reduceOutputFormat() {
        Map<String, Object> model = Map.of("company", "schegge.de");
        Template template = templateBuilder.getTemplate("test", "<#outputformat 'HTML'>${company}: ${name}</#outputformat>").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de: Jens Kaiser", template.process(Map.of("name", "Jens Kaiser")));
        assertEquals(new ReductionStatus(6, 6, 4), reductionStatus);
    }

    @Test
    void reduceWithoutStatus() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        String input = "<#list seq as s>${company}/${s} </#list>";
        Template template = templateBuilder.getTemplate("test", input);
        assertNotNull(template.reduce(reduceModel));
    }

    @Test
    void demo() {
        String input = """
                <#switch flag>
                <#case 1>${company}
                <#case 2>${name!'Jens'}
                <#case 3><#if name??>${name}<#else>Anonymous</#if>
                <#default>default
                </#switch>""";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(Map.of("flag", 3), reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals("Jens Kaiser\n", reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 3)));
        assertEquals(new ReductionStatus(18, 6, 3), reductionStatus);
    }

    @Test
    void reduceHashListWithHashLiteral() {
        Template template = templateBuilder.with(ReductionFeature.UNROLL_LIST)
                .getTemplate("test", "<#list { 'a': 'A', 'b': 'B' } as k, v>${k}=${v} </#list>");
        Template reducedTemplate = template.reduce(Map.of(), reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals("a=A b=B ", reducedTemplate.process(Map.of()));
        assertEquals(new ReductionStatus(7, 11, 7), reductionStatus);
    }

    public record Artist(String name, String band) {

    }

    @Test
    void reduceHashList() {
        Template template = templateBuilder.with(ReductionFeature.UNROLL_LIST).with(ReductionFeature.MERGE_CONSTANT_FRAGMENTS)
                .getTemplate("test", "<#list map as k sorted asc, v>${k} (${v.name} ${v.band}) </#list>");
        Map<String, Object> reduceModel = Map.of(
                "map", Map.of("bobby", new Artist("Bobby Hatfield", null), "bill", new Artist("Bill Medley", null)));
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        Map<String, Object> processModel = Map.of(
                "map", Map.of("bobby", new Artist("Bobby Hatfield", "TRB"), "bill", new Artist("Bill Medley", "TRB")));
        assertEquals("bill (Bill Medley TRB) bobby (Bobby Hatfield TRB) ", reducedTemplate.process(processModel));
        assertEquals(new ReductionStatus(9, 15, 7), reductionStatus);
    }
}
