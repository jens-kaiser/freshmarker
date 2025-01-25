package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class SettingTest {
    private TemplateBuilder templateBuilder;

    @BeforeEach
    void setUp(TemplateBuilder templateBuilder) {
        this.templateBuilder = templateBuilder.withZoneId(ZoneId.of("Europe/Berlin"));
    }

    @ParameterizedTest
    @CsvSource(value = "test: ${42.23} - <#setting locale=\"en_US\">${42.23};test: 42,23 - 42.23", delimiterString = ";")
    void settingLocale(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = "test: ${date?date_time} - <#setting zone_id=\"Europe/London\">${date?date_time},test: 2001-08-24 12:34:56 - 2001-08-24 11:34:56")
    void settingZoneI(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        ZonedDateTime zonedDateTime = LocalDateTime.of(2001, Month.AUGUST, 24, 12, 34, 56).atZone(ZoneId.of("Europe/Berlin"));
        assertEquals(expected, template.process(Map.of("date", zonedDateTime.toInstant())));
    }

    @ParameterizedTest
    @CsvSource(value = "test: ${date} - <#setting date_format=\"dd. MMMM yyyy\">${date};test: 1968-08-24 - 24. August 1968", delimiterString = ";")
    void settingDateFormat(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("date", LocalDate.of(1968, Month.AUGUST, 24))));
    }

    @ParameterizedTest
    @CsvSource(value = "test: ${date} - <#setting time_format=\"hh:mm\">${date};test: 12:34:56 - 12:34", delimiterString = ";")
    void settingTimeFormat(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("date", LocalTime.of(12, 34, 56))));
    }

    @ParameterizedTest
    @CsvSource(value = "test: ${date} - <#setting datetime_format=\"dd. MMMM yyyy hh:mm\">${date};test: 1968-08-24 12:34:56 - 24. August 1968 12:34", delimiterString = ";")
    void settingDateTimeFormat(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("date", LocalDateTime.of(1968, Month.AUGUST, 24, 12, 34, 56))));
    }

    @Test
    void unknownSetting() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "<#setting gonzo=''>");
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }
}