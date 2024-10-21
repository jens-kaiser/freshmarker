package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.model.primitive.TemplateVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuiltInVariableTest {
    private TemplateBuilder templateBuilder;

    @BeforeEach
    void setUp() {
        templateBuilder = new Configuration().builder().withLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${.locale},test: de_DE",
            "test: ${.locale?lang},test: de",
            "test: ${.locale?language},test: de",
            "test: ${.locale?country},test: DE",
            "test: ${.lang},test: de",
            "test: ${.language},test: de",
            "test: ${.country},test: DE",
    })
    void builtInVariables(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${'1.0.2'?version},test: 1.0.2",
            "test: ${'1.0.2'?version?major},test: 1",
            "test: ${'1.0.2'?version?minor},test: 0",
            "test: ${'1.0.2'?version?patch},test: 2",
            "test: ${'1.0.2'?version?is_before('1.0.3')},test: yes",
            "test: ${'1.0.2'?version?is_before('1.1.0')},test: yes",
            "test: ${'1.0.2'?version?is_before('2.0.0')},test: yes",
            "test: ${'1.0.2'?version?is_before(after)},test: yes",
            "test: ${'1.0.2'?version?is_equal('1.0.2')},test: yes",
            "test: ${'2.0.2'?version?is_after('1.0.0')},test: yes",
            "test: ${'1.1.2'?version?is_after('1.0.0')},test: yes",
            "test: ${'1.0.2'?version?is_after('1.0.0')},test: yes",
            "test: ${'1.0.2'?version?is_after('1.0.0'?version)},test: yes",
            "test: ${'1.0.2'?version?is_before('1.0.2')},test: no",
            "test: ${'1.0.2'?version?is_before('1.0.0')},test: no",
            "test: ${'1.0.2'?version?is_equal('1.0.3')},test: no",
            "test: ${'1.0.2'?version?is_equal('1.1.2')},test: no",
            "test: ${'1.0.2'?version?is_equal('2.0.2')},test: no",
            "test: ${'1.0.2'?version?is_after('1.1.0')},test: no",
            "test: ${'1.0.2'?version?is_after('1.0.2')},test: no",
            "test: ${version?is_before('1.0.0'?version)},test: no",
            "test: ${version?is_after(after)},test: no",
            "test: ${version < after},test: yes",
            "test: ${version <= after},test: yes",
            "test: ${after > version},test: yes",
            "test: ${after >= version},test: yes",
            "test: ${version > after},test: no",
            "test: ${version >= after},test: no",
            "test: ${after < version},test: no",
            "test: ${after <= version},test: no",
    })
    void version(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("version", new TemplateVersion("1.0.2"), "after", new TemplateVersion("1.1.0"))));
    }

    @Test
    void now() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${.now?date}");
        assertEquals("test: " + LocalDate.now(), template.process(Map.of()));
    }

    @Test
    void invalidVersion() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "${'1.0'?version}");
        Map<String, Object> model = Map.of();
        assertThrows(IllegalStateException.class, () -> template.process(model));
    }

    @Test
    void invalidIsBeforeWithoutParameter() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "${'1.6.3'?version?is_before}");
        Map<String, Object> model = Map.of();
        assertThrows(IllegalArgumentException.class, () -> template.process(model));
    }

    @Test
    void invalidIsBeforeWithWrongParameterType() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "${'1.6.3'?version?is_before(42)}");
        Map<String, Object> model = Map.of();
        assertThrows(IllegalStateException.class, () -> template.process(model));
    }

    @Test
    void unknownBuiltInVariable() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${.gonzo}");
        Map<String, Object> model = Map.of();
        assertThrows(IllegalStateException.class, () -> template.process(model));
    }
}