package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SliceAndRangeInterpolationTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "${(1..10)?size};10",
            "${(1..10)?lower};1",
            "${(1..10)?upper};10",
            "${(1..<10)?join};1, 2, 3, 4, 5, 6, 7, 8, 9",
            "${(1..<10)?reverse?join};9, 8, 7, 6, 5, 4, 3, 2, 1",
            "${(1..)?lower};1",
    }, delimiterString = ";")
    void interpolationRangeBuiltIns(String input, String expected) throws ParseException {
        Template template = configuration.builder().getTemplate("test", input);
        assertEquals(expected, template.process(Map.of()));
    }

    @Test
    void interpolationSlice() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${list[2..4]?join}");
        assertEquals("test: 3, 4, 5", template.process(Map.of("list", List.of(1, 2, 3, 4, 5, 6, 7))));
    }

    @Test
    void interpolationInvertedSlice() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${list[4..2]?join}");
        Map<String, Object> model = Map.of("list", List.of(1, 2, 3, 4, 5, 6, 7));
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "0,3",
            "1,4",
            "2,5",
            "3,6",
            "4,7"
    })
    void interpolationSliceRightUnbound(int index, int expected) throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${list[2..][i]}");
        assertEquals("test: " + expected, template.process(Map.of("list", List.of(1, 2, 3, 4, 5, 6, 7), "i", index)));
    }

    @Test
    void interpolationRangeRightUnbound() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${(0..)[100]}");
        assertEquals("test: 100", template.process(Map.of()));
    }

    @Test
    void interpolationRange() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${(0..20)[10]}");
        assertEquals("test: 10", template.process(Map.of()));
    }

    @Test
    void invalidSliceUsage() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${map[1..3]}");
        Map<String, Object> model = Map.of("map", Map.of());
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "test: <#list (1..) as i>${i}</#list>,right unlimited range not supported at test:1:7 '<#list (1..) as i>${i}</#list>'",
            "test: ${(1..)?upper},unsupported builtin 'upper' for TemplateRightUnlimitedRange at test:1:7 '${(1..)?upper}'"
    })
    void invalidUnlimitedRangeUsage(String input, String message) throws ParseException {
        Template template = configuration.builder().getTemplate("test", input);
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
    void interpolationLimitedSliceOnRange(String input, int a, int b, int c, int d, String expected) throws ParseException {
        Template template = configuration.builder().getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("a", a, "b", b, "c", c, "d", d)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${(a..b)[c..]?join};1;7;2;test: 3, 4, 5, 6, 7",
            "test: ${(a..b)[c..]?join};7;1;3;test: 4, 3, 2, 1",
            "test: ${(a..)[b..][c..c+1]?join};1;2;3;test: 6, 7"
    }, delimiterString = ";")
    void interpolationUnlimitedSliceOnRange(String input, int a, int b, int c, String expected) throws ParseException {
        Template template = configuration.builder().getTemplate("test", input);
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
            "test: ${(start..*count)?size},test: 10",
    })
    void interpolationRangeExclusiveAndRangeLimited(String input, String expected) throws ParseException {
        Template template = configuration.builder().getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("count", 10, "start", 1)));
    }
}