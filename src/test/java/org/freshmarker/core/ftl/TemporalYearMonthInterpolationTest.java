package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Month;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TemporalYearMonthInterpolationTest {

    @ParameterizedTest
    @CsvSource({
            "test: ${year_month?is_leap},test: no",
            "test: ${leap_year_month?is_leap},test: yes",
            "test: ${year_month?easter},test: 2025-04-20"
    })
    void interpolationLeapYear(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("year_month", YearMonth.of(2025, Month.AUGUST), "leap_year_month", YearMonth.of(2004, Month.APRIL))));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${year_month},test: 2025-08",
            "test: ${leap_year_month},test: 2004-04",
            "test: ${year_month?year},test: 2025",
            "test: ${leap_year_month?year},test: 2004",
            "test: ${year_month?month},test: AUGUST",
            "test: ${leap_year_month?month},test: APRIL",
    })
    void interpolationYear(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.withLocale(Locale.GERMANY).getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("year_month", YearMonth.of(2025, Month.AUGUST), "leap_year_month", YearMonth.of(2004, Month.APRIL))));
    }
}