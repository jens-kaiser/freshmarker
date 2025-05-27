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

import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class LocalTimeInterpolationTest {

    private static final LocalTime LOCAL_TIME = LocalTime.of(12, 30, 45);

    @Test
    void interpolationLocalTimeString(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string('hh:mm')}");
        Map<String, Object> dataModel = Map.of("temporal", LocalTime.of(12, 34, 56));
        String result = template.process(dataModel);
        assertEquals("test: 12:34", result);
    }

    @Test
    void interpolationLocalTimeStringMissingParameter(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string}");
        Map<String, Object> dataModel = Map.of("temporal", LocalTime.of(12, 34, 56));
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @Test
    void interpolationLocalTimeStringInvalidParameter(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal?string(format)}");
        Map<String, Object> dataModel = Map.of("format", 42, "temporal", LocalTime.of(12, 34, 56));
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "long;test: 12:30:45 MEZ;test: 12:30:45\u202FPM CET",
            "full;test: 12:30:45 Mitteleuropäische Zeit;test: 12:30:45\u202FPM Central European Time",
            "medium;test: 12:30:45;test: 12:30:45\u202FPM",
            "short;test: 12:30;test: 12:30\u202FPM"
    }, delimiterString = ";")
    void interpolationLocalTimeWithFormatter(String pattern, String expectedDe, String expectedUs, TemplateBuilder templateBuilder) throws ParseException {
        Template templateDe = templateBuilder.withLocale(Locale.GERMANY).withTimeFormat(pattern).getTemplate("test", "test: ${temporal}");
        Template templateUs = templateBuilder.withLocale(Locale.US).withTimeFormat(pattern).getTemplate("test", "test: ${temporal}");
        assertEquals(expectedDe, templateDe.process(Map.of("temporal", LOCAL_TIME)));
        assertEquals(expectedUs, templateUs.process(Map.of("temporal", LOCAL_TIME)));
    }

    @Test
    void interpolationLocalTime(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${temporal}");
        String result = template.process(Map.of("temporal", LocalTime.of(12, 30, 45)));
        assertEquals("test: 12:30:45", result);
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${temporal},test: 12:30:45",
            "test: ${temporal?c},test: 12:30:45",
            "test: ${temporal?time},test: 12:30:45"
    })
    void interpolationLocalTime(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> dataModel = Map.of("temporal", LOCAL_TIME);
        assertEquals(expected, template.process(dataModel));
    }
}