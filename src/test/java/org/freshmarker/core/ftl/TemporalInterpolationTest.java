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
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TemporalInterpolationTest {

    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(1968, Month.AUGUST, 24, 12, 30, 45);
    private static final Map<String, Object> TEMPORAL = Map.of("temporal", LOCAL_DATE_TIME);

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${temporal?string('dd. MMMM yyyy hh:mm')};test: 24. August 1968 12:30",
            "test: ${temporal?string('long')};test: 24. August 1968, 12:30:45 MEZ",
            "test: ${temporal?string('full')};test: Samstag, 24. August 1968, 12:30:45 Mitteleuropäische Zeit",
            "test: ${temporal?string('medium')};test: 24.08.1968, 12:30:45",
            "test: ${temporal?string('short')};test: 24.08.68, 12:30"
    }, delimiterString = ";")
    void interpolationLocalDateTimeString(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.withLocale(Locale.GERMANY).getTemplate("test", input);
        String result = template.process(TEMPORAL);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "long;test: 24. August 1968, 12:30:45 Z;test: August 24, 1968, 12:30:45\u202FPM Z",
            "full;test: Samstag, 24. August 1968, 12:30:45 Z;test: Saturday, August 24, 1968, 12:30:45\u202FPM Z",
            "medium;test: 24.08.1968, 12:30:45;test: Aug 24, 1968, 12:30:45\u202FPM",
            "short;test: 24.08.68, 12:30;test: 8/24/68, 12:30\u202FPM"
    }, delimiterString = ";")
    void interpolationLocalDateTimeWithFormatter(String pattern, String expectedDe, String expectedUs, TemplateBuilder templateBuilder) throws ParseException {
        Template templateDe = templateBuilder.withLocale(Locale.GERMANY).withDateTimeFormat(pattern, ZoneOffset.UTC).getTemplate("test", "test: ${temporal}");
        Template templateUs = templateBuilder.withLocale(Locale.US).withDateTimeFormat(pattern, ZoneOffset.UTC).getTemplate("test", "test: ${temporal}");
        assertEquals(expectedDe, templateDe.process(Map.of("temporal", LOCAL_DATE_TIME)));
        assertEquals(expectedUs, templateUs.process(Map.of("temporal", LOCAL_DATE_TIME)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal?string}",
            "test: ${temporal?at_zone}",
            "test: ${temporal?at_zone('42')}"
    })
    void interpolationLocalDateTimeWithFailure(String input, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertThrows(ProcessException.class, () -> template.process(TEMPORAL));
    }

    @Test
    void interpolationLocalDateTimeStringInvalidParameter(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string(format)}");
        Map<String, Object> dataModel = Map.of("format", 42, "temporal", LOCAL_DATE_TIME);
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource({
            "${temporal},1968-08-24 12:30:45",
            "${temporal?c},1968-08-24T12:30:45",
            "${temporal?date},1968-08-24",
            "${temporal?time},12:30:45",
            "${temporal?easter},1968-04-14",
    })
    void interpolationLocalDateTime(String templateString, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: " + templateString);
        assertEquals("test: " + expected, template.process(TEMPORAL));
    }

    @ParameterizedTest
    @CsvSource({
            "${temporal?easter},1968-04-14",
            "${temporal?easter},2025-04-20",
            "${temporal?easter},2121-04-06"
    })
    void interpolationEaster(String templateString, LocalDate date, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: " + templateString);
        assertEquals("test: " + date, template.process(Map.of("temporal", date)));
    }

    @Test
    void interpolationDuration(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal}");
        Map<String, Object> dataModel = Map.of("temporal", Duration.of(43, ChronoUnit.MINUTES));
        assertEquals("test: PT43M", template.process(dataModel));
    }

    @Test
    void interpolationLocalDateTimeAtZone(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin')}");
        assertEquals("test: 1968-08-24 12:30:45 Europe/Berlin", template.process(TEMPORAL));
    }

    @Test
    void interpolationLocalDateTimeAtZoneC(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin')?c}");
        assertEquals("test: 1968-08-24T12:30:45+01:00[Europe/Berlin]", template.process(TEMPORAL));
    }

    @Test
    void interpolationZonedDateTimeAtZone(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin')}");
        String result = template.process(Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("UTC"))));
        assertEquals("test: 1968-08-24 01:30:45 Europe/Berlin", result);
    }

    @Test
    void interpolationInvalidAtZone(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?at_zone('Europe/Berlin','Europe/London')}");
        Map<String, Object> model = Map.of("temporal", LOCAL_DATE_TIME.atZone(ZoneId.of("UTC")));
        ProcessException exception = assertThrows(ProcessException.class, () -> template.process(model));
        assertEquals("invalid parameter count:2 at test:1:7 '${temporal?at_zone('Europe/Berlin','Europe/London')}'", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${period1 + period2},test: P3D",
            "test: ${period1 - period1},test: P0D",
            "test: ${period1 - period2},test: P-1D"
    })
    void periodOperationPeriod(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = Map.of("period1", Period.of(0, 0, 1), "period2", Period.of(0, 0, 2));
        assertEquals(expected, template.process(model));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test: ${temporal * period}",
            "test: ${temporal + 1.0}",
            "test: ${temporal + 1.0?float}",
            "test: ${temporal + '1'}",
            "test: ${period / period}"
    })
    void invalidOperation(String input, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = Map.of("temporal", LOCAL_DATE_TIME.toLocalDate(), "period", Period.of(0, 0, 3));
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test: ${zoneddatetime?supports('YEARS')}",
            "test: ${zoneddatetime?supports('MONTHS')}",
            "test: ${zoneddatetime?supports('DAYS')}",
            "test: ${instant?supports('DAYS')}",
            "test: ${localdatetime?supports('YEARS')}",
            "test: ${localdatetime?supports('MONTHS')}",
            "test: ${localdatetime?supports('DAYS')}",
            "test: ${localdate?supports('YEARS')}",
            "test: ${localdate?supports('MONTHS')}",
            "test: ${localdate?supports('DAYS')}",
    })
    void supportsDate(String input, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = Map.of(
                "localdatetime", LocalDateTime.now(),
                "localdate", LocalDate.now(),
                "zoneddatetime", ZonedDateTime.now(),
                "instant", Instant.now()
        );
        assertEquals("test: yes", template.process(model));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test: ${zoneddatetime?supports('HOURS')}",
            "test: ${zoneddatetime?supports('MINUTES')}",
            "test: ${zoneddatetime?supports('SECONDS')}",
            "test: ${instant?supports('HOURS')}",
            "test: ${instant?supports('MINUTES')}",
            "test: ${instant?supports('SECONDS')}",
            "test: ${localdatetime?supports('HOURS')}",
            "test: ${localdatetime?supports('MINUTES')}",
            "test: ${localdatetime?supports('SECONDS')}",
            "test: ${localtime?supports('HOURS')}",
            "test: ${localtime?supports('MINUTES')}",
            "test: ${localtime?supports('SECONDS')}",
    })
    void supportsTime(String input, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = Map.of(
                "localdatetime", LocalDateTime.now(),
                "localtime", LocalTime.now(),
                "zoneddatetime", ZonedDateTime.now(),
                "instant", Instant.now()
        );
        assertEquals("test: yes", template.process(model));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test: ${localdate?supports('HOURS')}",
            "test: ${localdate?supports('MINUTES')}",
            "test: ${localdate?supports('SECONDS')}",
            "test: ${localtime?supports('YEARS')}",
            "test: ${localtime?supports('MONTHS')}",
            "test: ${localtime?supports('DAYS')}",
            "test: ${instant?supports('YEARS')}",
            "test: ${instant?supports('MONTHS')}",
    })
    void supportsNot(String input, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = Map.of(
                "localdate", LocalDate.now(),
                "localtime", LocalTime.now(),
                "instant", Instant.now()
        );
        assertEquals("test: no", template.process(model));
    }

    @ParameterizedTest
    @EnumSource(value = ChronoUnit.class, mode = Mode.EXCLUDE, names = { "YEARS", "MONTHS", "DAYS", "HOURS", "MINUTES", "SECONDS"})
    void unsupported(ChronoUnit unit, TemplateBuilder templateBuilder) throws ParseException {
        String name = unit.name();
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?supports('" + name + "')}");
        Map<String, Object> model = Map.of("temporal", LocalDateTime.now());
        ProcessException exception = assertThrows(ProcessException.class, () ->template.process(model));
        assertEquals("unsupported temporal unit at test:1:7 '${temporal?supports('" + name + "')}'", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "localdatetime1 > localdatetime2", "localdatetime1 >= localdatetime2",
            "localdatetime2 < localdatetime1", "localdatetime2 <= localdatetime1",
            "localdate1 > localdate2", "localdate1 >= localdate2",
            "localdate2 < localdate1", "localdate2 <= localdate1",
            "localtime1 > localtime2", "localtime1 >= localtime2",
            "localtime2 < localtime1", "localtime2 <= localtime1",
            "zoneddatetime1 > zoneddatetime2", "zoneddatetime1 >= zoneddatetime2",
            "zoneddatetime2 < zoneddatetime1", "zoneddatetime2 <= zoneddatetime1",
            "instant1 > instant2", "instant1 >= instant2",
            "instant2 < instant1", "instant2 <= instant1",
    })
    void relation(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("relation", "test: ${" + input + "}");
        Map<String, Object> model = Map.ofEntries(
                Map.entry("localdatetime1", LocalDateTime.now()),
                Map.entry("localtime1", LocalTime.now()),
                Map.entry("localdate1", LocalDate.now()),
                Map.entry("zoneddatetime1", ZonedDateTime.now()),
                Map.entry("offsetdatetime1", OffsetDateTime.now()),
                Map.entry("instant1", Instant.now()),
                Map.entry("localdatetime2", LocalDateTime.now().minusDays(1)),
                Map.entry("localtime2", LocalTime.now().minusSeconds(42)),
                Map.entry("localdate2", LocalDate.now().minusDays(1)),
                Map.entry("zoneddatetime2", ZonedDateTime.now().minusDays(1)),
                Map.entry("offsetdatetime2", OffsetDateTime.now()),
                Map.entry("instant2", Instant.now().minusSeconds(42))
        );
        assertEquals("test: yes", template.process(model));
    }
}