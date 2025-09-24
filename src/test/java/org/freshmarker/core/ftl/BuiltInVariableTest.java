package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateVersion;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class BuiltInVariableTest {

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
    void builtInVariables(String templateSource, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${'1.0.2'?version},test: 1.0.2",
            "test: ${'1.0.2'?version?major},test: 1",
            "test: ${'1.0.2'?version.major},test: 1",
            "test: ${'1.0.2'?version?minor},test: 0",
            "test: ${'1.0.2'?version.minor},test: 0",
            "test: ${'1.0.2'?version?patch},test: 2",
            "test: ${'1.0.2'?version.patch},test: 2",
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
    void version(String templateSource, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("version", new TemplateVersion("1.0.2"), "after", new TemplateVersion("1.1.0"))));
    }

    @Test
    void now(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.withZoneId(ZoneOffset.UTC).getTemplate("test", "test: ${.now?date}");
        assertEquals("test: " + LocalDate.now(ZoneOffset.UTC), template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource({
            "${'1.0'?version}",
            "${.gonzo}",
            "${'1.6.3'?version?is_before(42)}"
    })
    void invalidVersion(String input, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void invalidVersion(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "${'1.6.3'?version?is_before}");
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void unknownAttribute(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "${'1.6.3'?version.micro}");
        Map<String, Object> dataModel = Map.of();
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @Test
    void systemProperties(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "${.env['java.version']}");
        assertEquals(System.getProperties().getProperty("java.version"), template.process(Map.of()));
    }

    @Test
    void systemEnvironment(TemplateBuilder templateBuilder) {Template template = templateBuilder.getTemplate("test", "${.env['java.version']}");
        assertEquals(System.getProperties().getProperty("java.version"), template.process(Map.of()));
        System.err.println(System.getProperties().entrySet().stream().map(e -> e.getKey() + "->" + e.getValue()).collect(Collectors.joining("\n")));
        System.err.println(System.getenv().entrySet().stream().map(e -> e.getKey() + "->" + e.getValue()).collect(Collectors.joining("\n")));
    }
}