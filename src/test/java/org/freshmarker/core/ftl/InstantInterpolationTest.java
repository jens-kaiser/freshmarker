package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class InstantInterpolationTest {

    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(1968, Month.AUGUST, 24, 12, 30, 45);

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal},test: 1968-08-24 11:30:45 Z",
            "test: ${temporal?c},test: 1968-08-24T11:30:45Z",
            "test: ${temporal?string('dd. MMMM yyyy hh:mm')},test: 24. August 1968 11:30",
            "test: ${temporal?date},test: 1968-08-24",
            "test: ${temporal?easter},test: 1968-04-14",
    })
    void interpolationInstant(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("Europe/Berlin")).toInstant());
        assertEquals(expected, template.process(dataModel));
    }

    @Test
    void interpolationInstantTime(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.withZoneId(ZoneId.of("UTC")).getTemplate("test", "test: ${temporal?time}");
        String result = template.process(Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("UTC")).toInstant()));
        assertEquals("test: 12:30:45", result);
    }

    @Test
    void interpolationInstantAtZone(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin')}");
        String result = template.process(Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("UTC")).toInstant()));
        assertEquals("test: 1968-08-24 01:30:45 Europe/Berlin", result);
    }
}