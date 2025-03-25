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

import java.time.Period;
import java.time.Year;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TemporalYearInterpolationTest {

    public static final Year YEAR_2025 = Year.of(2025);
    public static final Year YEAR_2004 = Year.of(2004);

    @ParameterizedTest
    @CsvSource({
            "test: ${year?is_leap},test: no",
            "test: ${leap_year?is_leap},test: yes",
            "test: ${year},test: 2025",
            "test: ${leap_year},test: 2004",
            "test: ${year?year},test: 2025",
            "test: ${leap_year?year},test: 2004",
    })
    void interpolationYear(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("year", YEAR_2025, "leap_year", YEAR_2004)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${year + 1},test: 2026",
            "test: ${leap_year + 1},test: 2005",
            "test: ${year - 1},test: 2024",
            "test: ${leap_year - 1},test: 2003",
    })
    void addNumber(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("year", YEAR_2025, "leap_year", YEAR_2004)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${year + period},test: 2027",
            "test: ${leap_year + period},test: 2006",
            "test: ${year - period},test: 2023",
            "test: ${leap_year - period},test: 2002",
    })
    void addPeriod(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("year", YEAR_2025, "leap_year", YEAR_2004, "period", Period.ofYears(2));
        assertEquals(expected, template.process(dataModel));
    }

    @Test
    void unsupportedOperation(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "test: ${year * 2}");
        assertThrows(ProcessException.class, () -> template.process(Map.of("year", YEAR_2025)));
    }
}