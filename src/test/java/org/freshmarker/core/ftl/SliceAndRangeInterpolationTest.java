package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class SliceAndRangeInterpolationTest {

    @ParameterizedTest
    @CsvSource(value = {
            "${(1..10)?size};10",
            "${(1..10)?lower};1",
            "${(1..10)?upper};10",
            "${(1..<10)?join};1, 2, 3, 4, 5, 6, 7, 8, 9",
            "${(1..<10)?reverse?join};9, 8, 7, 6, 5, 4, 3, 2, 1",
            "${(1..)?lower};1",
    }, delimiterString = ";")
    void interpolationRangeBuiltIns(String input, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of()));
    }

    @Test
    void interpolationSlice(TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${list[2..4]?join}");
        assertEquals("test: 3, 4, 5", template.process(Map.of("list", List.of(1, 2, 3, 4, 5, 6, 7))));
    }

    @Test
    void interpolationInvertedSlice(TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${list[4..2]?join}");
        assertEquals("test: 5, 4, 3", template.process(Map.of("list", List.of(1, 2, 3, 4, 5, 6, 7))));
    }

    @ParameterizedTest
    @CsvSource({
            "0,3",
            "1,4",
            "2,5",
            "3,6",
            "4,7"
    })
    void interpolationSliceRightUnbound(int index, int expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${list[2..][i]}");
        assertEquals("test: " + expected, template.process(Map.of("list", List.of(1, 2, 3, 4, 5, 6, 7), "i", index)));
    }

    @Test
    void interpolationRangeRightUnbound(TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(0..)[100]}");
        assertEquals("test: 100", template.process(Map.of()));
    }

    @Test
    void interpolationRange(TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(0..20)[10]}");
        assertEquals("test: 10", template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource({
            "${1..<}",
            "${1..*}"
    })
    void invalidUnlimitedSliceUsages(String input, TemplateBuilder builder) throws ParseException {
        assertThrows(ParsingException.class, () -> builder.getTemplate("test", input));
    }

    @Test
    void invalidSliceUsage(TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${map[1..3]}");
        Map<String, Object> model = Map.of("map", Map.of());
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void indexOutOfRangeOnRange(TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "test: ${(0..3)[6]}");
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "test: <#list (1..) as i>${i}</#list>,right unlimited range not supported at test:1:7 '<#list (1..) as i>${i}</#list>'",
            "test: ${(1..)?upper},unsupported builtin 'upper' for TemplateRightUnlimitedRange at test:1:7 '${(1..)?upper}'"
    })
    void invalidUnlimitedRangeUsage(String input, String message, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", input);
        Map<String, Object> model = Map.of("map", Map.of());
        ProcessException exception = assertThrows(ProcessException.class, () -> template.process(model));
        assertEquals(message, exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${(a..b)[c..d]?join};1;7;2;4;test: 3, 4, 5",
            "test: ${(a..b)[c..d]?join};7;1;2;4;test: 5, 4, 3",
            "test: ${(a..)[c..d]?join};1;7;2;4;test: 3, 4, 5",
    }, delimiterString = ";")
    void interpolationLimitedSliceOnRange(String input, int a, int b, int c, int d, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("a", a, "b", b, "c", c, "d", d)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${(a..b)[c..]?join};1;7;2;test: 3, 4, 5, 6, 7",
            "test: ${(a..b)[c..]?join};7;1;3;test: 4, 3, 2, 1",
            "test: ${(a..)[b..][c..c+1]?join};1;2;3;test: 6, 7"
    }, delimiterString = ";")
    void interpolationUnlimitedSliceOnRange(String input, int a, int b, int c, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("a", a, "b", b, "c", c)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${(1..9)?size},test: 9",
            "test: ${(1..(10-1))?size},test: 9",
            "test: ${(1..10-1)?size},test: 9",
            "test: ${(1..<10)?size},test: 9",
            "test: ${(1..count-1)?size},test: 9",
            "test: ${(1..<count)?size},test: 9",
            "test: ${(1..1+5-1)?size},test: 5",
            "test: ${(1..*5)?size},test: 5",
            "test: ${(3..*5)?upper},test: 7",
            "test: ${(start..*count)?size},test: 10",
            "test: ${(0..*5)?join(';')},test: 0;1;2;3;4",
            "test: ${(0..*5)?reverse?join(';')},test: 4;3;2;1;0",
            "test: ${(4..*-5)?join(';')},test: 4;3;2;1;0",
            "test: ${(4..*-5)?reverse?join(';')},test: 0;1;2;3;4",
    })
    void interpolationRangeExclusiveAndRangeLimited(String input, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("count", 10, "start", 1)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${(0..10)[2..4]?join};test: 2, 3, 4",
            "test: ${(10..0)[2..4]?join};test: 8, 7, 6",
            "test: ${(0..10)[4..2]?join};test: 4, 3, 2",
            "test: ${(10..0)[4..2]?join};test: 6, 7, 8",

            "test: ${(2..4)[0..2]?join};test: 2, 3, 4",
            "test: ${(2..4)[2..0]?join};test: 4, 3, 2",
            "test: ${(4..2)[0..2]?join};test: 4, 3, 2",
            "test: ${(4..2)[2..0]?join};test: 2, 3, 4",

            "test: ${(2..4)[0..]?join};test: 2, 3, 4",
            "test: ${(4..2)[0..]?join};test: 4, 3, 2",

            "test: ${(-2..-4)[0..2]?join};test: -2, -3, -4",
            "test: ${(-2..-4)[2..0]?join};test: -4, -3, -2",
            "test: ${(-4..-2)[0..2]?join};test: -4, -3, -2",
            "test: ${(-4..-2)[2..0]?join};test: -2, -3, -4",

            "test: ${(-2..-4)[0..]?join};test: -2, -3, -4",
            "test: ${(-4..-2)[0..]?join};test: -4, -3, -2",

            "test: ${(0..10)[2..2]?join};test: 2",
            "test: ${(10..0)[2..2]?join};test: 8",
    }, delimiterString = ";")
    void interpolationRangeSlices(String input, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("slices", input);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${list1[2..4]?join};test: 2, 3, 4",
            "test: ${list2[2..4]?join};test: 8, 7, 6",
            "test: ${list1[4..2]?join};test: 4, 3, 2",
            "test: ${list2[4..2]?join};test: 6, 7, 8",

            "test: ${list3[0..2]?join};test: 2, 3, 4",
            "test: ${list3[2..0]?join};test: 4, 3, 2",
            "test: ${list4[0..2]?join};test: 4, 3, 2",
            "test: ${list4[2..0]?join};test: 2, 3, 4",

            "test: ${list3[0..]?join};test: 2, 3, 4",
            "test: ${list4[0..]?join};test: 4, 3, 2",

            "test: ${list1[2..2]?join};test: 2",
            "test: ${list2[2..2]?join};test: 8",
    }, delimiterString = ";")
    void interpolationSequenceSlices(String input, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("slices", input);
        List<Integer> list = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        assertEquals(expected, template.process(Map.of(
                "list1", list, "list2", list.reversed(), "list3", List.of(2,3,4), "list4", List.of(4,3,2)
        )));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${string1[2..4]};test: 234",
            "test: ${string2[2..4]};test: 876",
            "test: ${string1[4..2]};test: 432",
            "test: ${string2[4..2]};test: 678",

            "test: ${string3[0..2]};test: 234",
            "test: ${string3[2..0]};test: 432",
            "test: ${string4[0..2]};test: 432",
            "test: ${string4[2..0]};test: 234",

            "test: ${string3[0..]};test: 234",
            "test: ${string4[0..]};test: 432",

            "test: ${string1[2..2]};test: 2",
            "test: ${string2[2..2]};test: 8",
    }, delimiterString = ";")
    void interpolationStringSlices(String input, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("slices", input);
        assertEquals(expected, template.process(Map.of(
                "string1", "0123456789A", "string2", "A9876543210", "string3", "234", "string4", "432"
        )));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${(1..<1)?size},test: 0",
            "test: ${(start..<start)?size},test: 0",
            "test: ${(1..*0)?size},test: 0",
            "test: ${(start..*count)?size},test: 0",
            "test: ${(1..<1)?join},test: ",
            "test: ${(start..<start)?join},test: ",
            "test: ${(1..*0)?join},test: ",
            "test: ${(start..*count)?join},test: ",
            "test: <#list (start..*count) as i>{$i}</#list>,test: ",
    }, ignoreLeadingAndTrailingWhitespace = false)
    void emptyRange(String input, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("count", 0, "start", 1)));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "${(0..10)[-1..0]}",
        "${(0..10)[0..-1]}",
        "${(0..10)[-10..]}",
        "${(0..10)[0..<0]}",
        "${(0..<0)[0..]}",
        "${(0..<0)[0..1]}"
    })
    void invalidSlices(String input, TemplateBuilder builder) {
        Template template = builder.getTemplate("slices", input);
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void interpolationUnlimitedRanges(TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "<#var x=10..>test: ${(0..)[x][10]}");
        assertEquals("test: 20", template.process(Map.of()));
    }
}