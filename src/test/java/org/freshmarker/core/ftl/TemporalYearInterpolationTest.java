package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Year;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TemporalYearInterpolationTest {

    @ParameterizedTest
    @CsvSource({
            "test: ${year?is_leap},test: no",
            "test: ${leap_year?is_leap},test: yes",
    })
    void interpolationLeapYear(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("year", Year.of(2025), "leap_year", Year.of(2004))));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${year},test: 2025",
            "test: ${leap_year},test: 2004",
            "test: ${year?year},test: 2025",
            "test: ${leap_year?year},test: 2004",
    })
    void interpolationYear(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("year", Year.of(2025), "leap_year", Year.of(2004))));
    }
}