package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.core.ProcessException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class LocalDatelInterpolationTest {

    public static final Map<String, Object> LOCAL_DATE = Map.of("temporal", LocalDate.of(1968, Month.AUGUST, 24));

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal},test: 1968-08-24",
            "test: ${temporal?c},test: 1968-08-24",
            "test: ${temporal?date},test: 1968-08-24",
            "test: ${temporal?string('d. MMMM yyyy')},test: 24. August 1968",
            "test: ${temporal?easter},test: 1968-04-14"
    })
    void interpolationLocalDate(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(LOCAL_DATE));
    }

    @Test
    void interpolationLocalDateStringMissingParameter(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string}");
        assertThrows(ProcessException.class, () -> template.process(LOCAL_DATE));
    }

    @Test
    void interpolationLocalDateStringInvalidParameter(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string(format)}");
        Map<String, Object> dataModel = Map.of("format", 42, "temporal", LocalDate.of(1968, Month.AUGUST, 24));
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "long;test: 24. August 1968;test: August 24, 1968",
            "full;test: Samstag, 24. August 1968;test: Saturday, August 24, 1968",
            "medium;test: 24.08.1968;test: Aug 24, 1968",
            "short;test: 24.08.68;test: 8/24/68"
    }, delimiterString = ";")
    void interpolationLocalDateWithFormatter(String pattern, String expectedDe, String expectedUs, TemplateBuilder templateBuilder) throws ParseException {
        Template templateDe = templateBuilder.withLocale(Locale.GERMANY).withDateFormat(pattern).getTemplate("test", "test: ${temporal}");
        Template templateUs = templateBuilder.withLocale(Locale.US).withDateFormat(pattern).getTemplate("test", "test: ${temporal}");
        assertEquals(expectedDe, templateDe.process(LOCAL_DATE));
        assertEquals(expectedUs, templateUs.process(LOCAL_DATE));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "de,test: <#list dates as date>${date?h(now)} </#list>,test: 1968-08-21 vorgestern gestern heute morgen übermorgen 1968-08-27 ",
            "en,test: <#list dates as date>${date?h(now)} </#list>,test: 1968-08-21 the day before yesterday yesterday today tomorrow the day after tomorrow 1968-08-27 ",
            "fr,test: <#list dates as date>${date?h(now)} </#list>,test: 1968-08-21 avant-hier hier aujourd'hui demain après-demain 1968-08-27 ",
            "de,test: ${.now?date?h},test: heute",
            "en,test: ${.now?date?h},test: today",
            "fr,test: ${.now?date?h},test: aujourd'hui",
    }, ignoreLeadingAndTrailingWhitespace = false)
    void interpolationLocalDateHuman(Locale locale, String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.withLocale(locale).getTemplate("test", input);
        LocalDate now = LocalDate.of(1968, Month.AUGUST, 24);
        List<LocalDate> dates = now.minusDays(3).datesUntil(now.plusDays(4)).toList();
        Map<String, Object> dataModel = Map.of("now", now, "dates", dates);
        assertEquals(expected, template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource({
            "P0D,${now?until}",
            "P1D,${yesterday?until}",
            "P2D,${dayBefore?until}",
            "P1D,${dayBefore?until(yesterday)}",
            "P3D,${dayBefore?until(tomorrow)}",
            "P0D,${now?since}",
            "P1D,${tomorrow?since}",
            "P2D,${tomorrow?since(yesterday)}",
    })
    void interpolateSinceAndUntil(String expected, String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.withZoneId(ZoneOffset.UTC).getTemplate("test", input);
        LocalDate now = LocalDate.now(ZoneOffset.UTC);
        LocalDate tomorrow = now.plusDays(1);
        LocalDate yesterday = now.minusDays(1);
        LocalDate dayBefore = now.minusDays(2);
        Map<String, Object> dataModel = Map.of("now", now, "yesterday", yesterday, "tomorrow", tomorrow, "dayBefore", dayBefore);
        assertEquals(expected, template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal + period},test: 1968-08-27",
            "test: ${temporal - period},test: 1968-08-21"
    })
    void dateOperationPeriod(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = Map.of("temporal", LOCAL_DATE.get("temporal"), "period", Period.of(0, 0, 3));
        assertEquals(expected, template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal + 1},test: 1968-08-25",
            "test: ${temporal - 1},test: 1968-08-23"
    })
    void dateOperationInteger(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        assertEquals(expected, template.process(LOCAL_DATE));
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
        Map<String, Object> model = Map.of("temporal", LOCAL_DATE.get("temporal"), "period", Period.of(0, 0, 3));
        assertThrows(ProcessException.class, () -> template.process(model));
    }
}