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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class OffsetDateTimeInterpolationTest {

    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(1968, Month.AUGUST, 24, 12, 30, 45);
    private static final OffsetDateTime OFFSET_DATE_TIME = LOCAL_DATE_TIME.atOffset(ZoneOffset.UTC);

    @ParameterizedTest
    @CsvSource(value = {
            //"long;test: 24. August 1968, 12:30:45 Z",
            //"full;test: Samstag, 24. August 1968, 12:30:45 Z",
            "medium;test: 24.08.1968, 12:30:45",
            "short;test: 24.08.68, 12:30"
    }, delimiterString = ";")
    void interpolationZonedDateTimeWithFormatter(String pattern, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.withLocale(Locale.GERMANY).withDateTimeFormat(pattern).getTemplate("test", "test: ${temporal}");
        String result = template.process(Map.of("temporal", OFFSET_DATE_TIME));
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal},test: 1968-08-24 12:30:45 +0100",
            "test: ${temporal?c},test: 1968-08-24T12:30:45+01:00",
            "test: ${temporal?string('dd. MMMM yyyy hh:mm')},test: 24. August 1968 12:30",
            "test: ${temporal?date_time},test: 1968-08-24 12:30:45",
            "test: ${temporal?date},test: 1968-08-24",
            "test: ${temporal?time},test: 12:30:45"
    })
    void interpolationZonedDateTime(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("temporal", LOCAL_DATE_TIME.atOffset(ZoneId.of("Europe/Berlin").getRules().getOffset(LOCAL_DATE_TIME)));
        assertEquals(expected, template.process(dataModel));
    }

    @Test
    void interpolationZonedDateTimeAtZone(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin')}");
        String result = template.process(Map.of("temporal", OFFSET_DATE_TIME));
        assertEquals("test: 1968-08-24 01:30:45 Europe/Berlin", result);
    }
}