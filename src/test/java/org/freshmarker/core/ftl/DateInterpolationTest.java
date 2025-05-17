package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class DateInterpolationTest {

    private static final GregorianCalendar CALENDAR = new GregorianCalendar(1968, Calendar.AUGUST, 24, 12, 30, 45);

    @ParameterizedTest
    @CsvSource({
            "${temporal},test: 1968-08-24",
            "${temporal?date},test: 1968-08-24",
            "${temporal?c},test: 1968-08-24",
    })
    void dateBuiltIns(String interpolation, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: " + interpolation);
        assertEquals(expected, template.process(Map.of("temporal", new java.sql.Date(CALENDAR.getTimeInMillis()))));
    }

    @ParameterizedTest
    @CsvSource({
            "${temporal}",
            "${temporal?time}",
            "${temporal?c}",
    })
    void interpolationTime(String input, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        String result = template.process(Map.of("temporal", new java.sql.Time(CALENDAR.getTimeInMillis())));
        assertEquals("12:30:45", result);
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal},test: 1968-08-24 12:30:45",
            "test: ${temporal?c},test: 1968-08-24T12:30:45",
            "test: ${temporal?date},test: 1968-08-24",
            "test: ${temporal?time},test: 12:30:45"
    })
    void interpolationDateTime(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        String result = template.process(Map.of("temporal", CALENDAR.getTime()));
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test: ${classicdatetime?supports('YEARS')}",
            "test: ${classicdatetime?supports('MONTHS')}",
            "test: ${classicdatetime?supports('DAYS')}",
            "test: ${classicdate?supports('YEARS')}",
            "test: ${classicdate?supports('MONTHS')}",
            "test: ${classicdate?supports('DAYS')}",
    })
    void supports(String input, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = Map.of(
                "classicdatetime", new Date(),
                "classicdate", new java.sql.Date(0)
        );
        assertEquals("test: yes", template.process(model));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test: ${classictime?supports('YEARS')}",
            "test: ${classictime?supports('MONTHS')}",
            "test: ${classictime?supports('DAYS')}",
    })
    void supportsNot(String input, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = Map.of("classictime", new Time(0));
        assertEquals("test: no", template.process(model));
    }
}

