package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TemporalDurationAndPeriodInterpolationTest {

    @Test
    void interpolationDuration(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal} ${+temporal}");
        Map<String, Object> dataModel = Map.of("temporal", Duration.of(43, ChronoUnit.MINUTES));
        assertEquals("test: PT43M PT43M", template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "de;P0D;test: ${temporal};test: P0D",
            "de;P0D;test: ${+temporal};test: P0D",
            "de;P0Y0M0D;test: ${temporal};test: P0D",
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
            "de;P0D;test: ${temporal?h};test: ",
            "de;P0Y0M0D;test: ${temporal?h};test: ",
            "de;P2Y4M1D;test: ${temporal?h};test: 2 Jahre, 4 Monate, 1 Tag",
            "de;P4M1D;test: ${temporal?h};test: 4 Monate, 1 Tag",
            "de;P2Y2D;test: ${temporal?h};test: 2 Jahre, 2 Tage",
            "en;P2Y4M1D;test: ${temporal?h};test: 2 years, 4 months, 1 day",
            "en;P4M1D;test: ${temporal?h};test: 4 months, 1 day",
            "en;P2Y2D;test: ${temporal?h};test: 2 years, 2 days",
    }, delimiterString = ";", ignoreLeadingAndTrailingWhitespace = false)
    void interpolationPeriod(Locale locale, Period period, String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
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
    void interpolationPeriodToNumbers(Period period, String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("temporal", period);
        assertEquals(expected, template.process(dataModel));
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
    @CsvSource({
            "period1 < period2,test: yes", "period1 <= period2,test: yes", "period1 <= period1,test: yes",
            "period2 > period1,test: yes", "period2 >= period1,test: yes", "period2 >= period2,test: yes",
            "period2 < period1,test: no", "period2 <= period1,test: no",
            "period1 > period2,test: no", "period1 >= period2,test: no",
    })
    void relationPeriod(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${" + input +"}");
        Map<String, Object> model = Map.of("period1", Period.of(0, 0, 1), "period2", Period.of(0, 0, 2));
        assertEquals(expected, template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "duration1 < duration2,test: yes", "duration1 <= duration2,test: yes", "duration1 <= duration1,test: yes",
            "duration2 > duration1,test: yes", "duration2 >= duration1,test: yes", "duration2 >= duration2,test: yes",
            "duration2 < duration1,test: no", "duration2 <= duration1,test: no",
            "duration1 > duration2,test: no", "duration1 >= duration2,test: no",
    })
    void relationDuration(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${" + input +"}");
        Map<String, Object> model = Map.of("duration1", Duration.ofMinutes(23), "duration2", Duration.ofMinutes(42));
        assertEquals(expected, template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "PT1H43M,test: PT-1H-43M",
            "PT1H-43M,test: PT-17M",
            "PT-1H43M,test: PT17M"
    })
    void negatedDuration(Duration duration, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${-temporal}");
        Map<String, Object> dataModel = Map.of("temporal", duration);
        assertEquals(expected, template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource({
            "P1M4D,test: P-1M-4D",
            "P1M-2D,test: P-1M2D",
            "P-1M-2D,test: P1M2D"
    })
    void negatedPeriod(Period period, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${-temporal}");
        Map<String, Object> dataModel = Map.of("temporal", period);
        assertEquals(expected, template.process(dataModel));
    }
}