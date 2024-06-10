package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SettingTest {
    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource(value = "test: ${42.23} - <#setting locale=\"en_US\">${42.23};test: 42,23 - 42.23", delimiterString = ";")
    void settingLocale(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = "test: ${date} - <#setting date_format=\"dd. MMMM yyyy\">${date};test: 1968-08-24 - 24. August 1968", delimiterString = ";")
    void settingDateFormat(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("date", LocalDate.of(1968, Month.AUGUST, 24))));
    }

    @ParameterizedTest
    @CsvSource(value = "test: ${date} - <#setting time_format=\"hh:mm\">${date};test: 12:34:56 - 12:34", delimiterString = ";")
    void settingTimeFormat(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("date", LocalTime.of(12, 34, 56))));
    }

    @ParameterizedTest
    @CsvSource(value = "test: ${date} - <#setting datetime_format=\"dd. MMMM yyyy hh:mm\">${date};test: 1968-08-24 12:34:56 - 24. August 1968 12:34", delimiterString = ";")
    void settingDateTimeFormat(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("date", LocalDateTime.of(1968, Month.AUGUST, 24, 12, 34, 56))));
    }

    @Test
    void unknownSetting() throws ParseException {
        Template template = configuration.getTemplate("test", "<#setting gonzo=''>");
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }
}