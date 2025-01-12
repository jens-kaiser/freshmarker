package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Month;
import java.time.MonthDay;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TemporalMonthDayInterpolationTest {

    @ParameterizedTest
    @CsvSource({
            "test: ${month_day},test: 08-24",
            "test: ${month_day?month},test: AUGUST",
    })
    void interpolationYear(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("month_day", MonthDay.of(Month.AUGUST, 24))));
    }
}