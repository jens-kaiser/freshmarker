package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TemporalInterpolationTest {

    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(1968, Month.AUGUST, 24, 12, 30, 45);
    private static final Map<String, Object> TEMPORAL = Map.of("temporal", LOCAL_DATE_TIME);
    public static final Map<String, Object> LOCAL_DATE = Map.of("temporal", LocalDate.of(1968, Month.AUGUST, 24));

    private TemplateBuilder templateBuilder;

    @BeforeEach
    void setUp() {
        templateBuilder = new Configuration().builder();
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal},test: 1968-08-24",
            "test: ${temporal?c},test: 1968-08-24",
            "test: ${temporal?date},test: 1968-08-24",
            "test: ${temporal?string('d. MMMM yyyy')},test: 24. August 1968"
    })
    void interpolationLocalDate(String input, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(LOCAL_DATE));
    }

    @Test
    void interpolationLocalDateStringMissingParameter() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string}");
        assertThrows(ProcessException.class, () -> template.process(LOCAL_DATE));
    }

    @Test
    void interpolationLocalDateStringInvalidParameter() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string(format)}");
        Map<String, Object> dataModel = Map.of("format", 42, "temporal", LocalDate.of(1968, Month.AUGUST, 24));
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @Test
    void interpolationLocalTimeString() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string('hh:mm')}");
        Map<String, Object> dataModel = Map.of("temporal", LocalTime.of(12, 34, 56));
        String result = template.process(dataModel);
        assertEquals("test: 12:34", result);
    }

    @Test
    void interpolationLocalTimeStringMissingParameter() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string}");
        Map<String, Object> dataModel = Map.of("temporal", LocalTime.of(12, 34, 56));
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @Test
    void interpolationLocalTimeStringInvalidParameter() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string(format)}");
        Map<String, Object> dataModel = Map.of("format", 42, "temporal", LocalTime.of(12, 34, 56));
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @Test
    void interpolationLocalDateTimeString() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string('dd. MMMM yyyy hh:mm')}");
        String result = template.process(TEMPORAL);
        assertEquals("test: 24. August 1968 12:30", result);
    }

    @Test
    void interpolationLocalDateTimeStringMissingParameter() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string}");
        assertThrows(ProcessException.class, () -> template.process(TEMPORAL));
    }

    @Test
    void interpolationLocalDateTimeStringInvalidParameter() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string(format)}");
        Map<String, Object> dataModel = Map.of("format", 42, "temporal", LOCAL_DATE_TIME);
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @Test
    void interpolationLocalTime() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal}");
        String result = template.process(Map.of("temporal", LocalTime.of(12, 30, 45)));
        assertEquals("test: 12:30:45", result);
    }

    @ParameterizedTest
    @CsvSource({
            "${temporal},1968-08-24 12:30:45",
            "${temporal?c},1968-08-24T12:30:45",
            "${temporal?date},1968-08-24",
            "${temporal?time},12:30:45"
    })
    void interpolationLocalDateTime(String templateString, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: " + templateString);
        assertEquals("test: " + expected, template.process(TEMPORAL));
    }

    @Test
    void interpolationDuration() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal}");
        Map<String, Object> dataModel = Map.of("temporal", Duration.of(43, ChronoUnit.MINUTES));
        assertEquals("test: PT43M", template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "de;P2Y4M1D;test: ${temporal};test: P2Y4M1D",
            "de;P4M1D;test: ${temporal};test: P4M1D",
            "de;P2Y2D;test: ${temporal};test: P2Y2D",
            "en;P2Y4M1D;test: ${temporal};test: P2Y4M1D",
            "en;P4M1D;test: ${temporal};test: P4M1D",
            "en;P2Y2D;test: ${temporal};test: P2Y2D",
            "de;P2Y4M1D;test: ${temporal?c};test: P2Y4M1D",
            "de;P4M1D;test: ${temporal?c};test: P4M1D",
            "de;P2Y2D;test: ${temporal?c};test: P2Y2D",
            "en;P2Y4M1D;test: ${temporal?c};test: P2Y4M1D",
            "en;P4M1D;test: ${temporal?c};test: P4M1D",
            "en;P2Y2D;test: ${temporal?c};test: P2Y2D",
            "de;P2Y4M1D;test: ${temporal?h};test: 2 Jahre, 4 Monate, 1 Tag",
            "de;P4M1D;test: ${temporal?h};test: 4 Monate, 1 Tag",
            "de;P2Y2D;test: ${temporal?h};test: 2 Jahre, 2 Tage",
            "en;P2Y4M1D;test: ${temporal?h};test: 2 years, 4 months, 1 day",
            "en;P4M1D;test: ${temporal?h};test: 4 months, 1 day",
            "en;P2Y2D;test: ${temporal?h};test: 2 years, 2 days",
    }, delimiterString = ";")
    void interpolationPeriod(Locale locale, Period period, String input, String expected) throws ParseException {
        Template template = templateBuilder.withLocale(locale).getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("temporal", period);
        assertEquals(expected, template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "P1Y2M3D;test: ${temporal?years?max(0)};test: 1",
            "P1Y2M3D;test: ${temporal?months?max(0)};test: 2",
            "P1Y2M3D;test: ${temporal?days?max(0)};test: 3",
    }, delimiterString = ";")
    void interpolationPeriodToNumbers(Period period, String input, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("temporal", period);
        assertEquals(expected, template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal},test: 12:30:45",
            "test: ${temporal?c},test: 12:30:45",
            "test: ${temporal?time},test: 12:30:45"
    })
    void interpolationLocalTime(String input, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("temporal", LOCAL_DATE_TIME.toLocalTime());
        assertEquals(expected, template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal},test: 1968-08-24 12:30:45 Europe/Berlin",
            "test: ${temporal?c},test: 1968-08-24T12:30:45+01:00[Europe/Berlin]",
            "test: ${temporal?string('dd. MMMM yyyy hh:mm')},test: 24. August 1968 12:30",
            "test: ${temporal?date_time},test: 1968-08-24 12:30:45",
            "test: ${temporal?date},test: 1968-08-24",
            "test: ${temporal?time},test: 12:30:45"
    })
    void interpolationZonedDateTime(String input, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("Europe/Berlin")));
        assertEquals(expected, template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal},test: 1968-08-24 11:30:45 Z",
            "test: ${temporal?c},test: 1968-08-24T11:30:45Z",
            "test: ${temporal?string('dd. MMMM yyyy hh:mm')},test: 24. August 1968 11:30",
            "test: ${temporal?date},test: 1968-08-24",
    })
    void interpolationInstant(String input, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("Europe/Berlin")).toInstant());
        assertEquals(expected, template.process(dataModel));
    }

    @Test
    void interpolationLocalDateTimeAtZone() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin')}");
        assertEquals("test: 1968-08-24 12:30:45 Europe/Berlin", template.process(TEMPORAL));
    }

    @Test
    void interpolationLocalDateTimeAtZoneC() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin')?c}");
        assertEquals("test: 1968-08-24T12:30:45+01:00[Europe/Berlin]", template.process(TEMPORAL));
    }

    @Test
    void interpolationInstantTime() throws ParseException {
        Template template = templateBuilder.withZoneId(ZoneId.of("UTC")).getTemplate("test", "test: ${temporal?time}");
        String result = template.process(Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("UTC")).toInstant()));
        assertEquals("test: 12:30:45", result);
    }

    @Test
    void interpolationInstantAtZone() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin')}");
        String result = template.process(Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("UTC")).toInstant()));
        assertEquals("test: 1968-08-24 01:30:45 Europe/Berlin", result);
    }

    @Test
    void interpolationZonedDateTimeAtZone() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin')}");
        String result = template.process(Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("UTC"))));
        assertEquals("test: 1968-08-24 01:30:45 Europe/Berlin", result);
    }

    @Test
    void interpolationZonedDateTimeZone() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?zone}");
        String result = template.process(Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("Europe/London"))));
        assertEquals("test: Europe/London", result);
    }

    @Test
    void interpolationInvalidAtZone() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin','Europe/London')}");
        Map<String, Object> model = Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("UTC")));
        ProcessException exception = assertThrows(ProcessException.class, () ->template.process(model));
        assertEquals("invalid parameter count:2 at test:1:7 '${temporal?at_zone('Europe/Berlin','Europe/London')}'", exception.getMessage());
    }

        @ParameterizedTest
    @CsvSource(value = {
            "de,test: <#list dates as date>${date?h(now)} </#list>,test: 1968-08-21 vorgestern gestern heute morgen übermorgen 1968-08-27 ",
            "en,test: <#list dates as date>${date?h(now)} </#list>,test: 1968-08-21 the day before yesterday yesterday today tomorrow the day after tomorrow 1968-08-27 ",
            "fr,test: <#list dates as date>${date?h(now)} </#list>,test: 1968-08-21 avant-hier hier aujourd'hui demain après-demain 1968-08-27 ",
    }, ignoreLeadingAndTrailingWhitespace = false)
    void interpolationLocalDateHuman(Locale locale, String input, String expected) throws ParseException {
        Template template = templateBuilder.withLocale(locale).getTemplate("test", input);
        LocalDate now = LocalDate.of(1968, Month.AUGUST, 24);
        List<LocalDate> dates = now.minusDays(3).datesUntil(now.plusDays(4)).toList();
        Map<String, Object> dataModel = Map.of("now", now, "dates", dates);
        assertEquals(expected, template.process(dataModel));
    }
}
