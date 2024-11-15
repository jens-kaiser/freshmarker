package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class StringInterpolationTest {

    private static final String TEXT = "The lazy Dog jumps over the Quick brown Fox";

    @ParameterizedTest
    @CsvSource({
            "test: ${text},test: The lazy Dog jumps over the Quick brown Fox",
            "test: ${text?upper_case},test: THE LAZY DOG JUMPS OVER THE QUICK BROWN FOX",
            "test: ${text?lower_case},test: the lazy dog jumps over the quick brown fox",
            "test: ${text?upper_case?lower_case},test: the lazy dog jumps over the quick brown fox"
    })
    void interpolationString(String templateSource, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("text", TEXT)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${a?boolean},test: yes",
            "test: ${b?boolean},test: no"
    })
    void interpolationBoolean(String templateSource, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("a", "true", "b", "false")));
    }

    @Test
    void invalidInterpolationBoolean(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${text?boolean}");
        Map<String, Object> model = Map.of("text", "gonzo");
        assertThrows(ProcessException.class, () ->  template.process(model));
    }

    @Test
    void interpolationTrim(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${text?trim}");
        assertEquals("test: text", template.process(Map.of("text", "  text  ")));
    }

    @Test
    void interpolationLength(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${text?length} ${text?trim?length}");
        assertEquals("test: 8 4", template.process(Map.of("text", "  text  ")));
    }

    @Test
    void interpolationDynamicKey(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${text[2]} ${text[3]}");
        assertEquals("test: x t", template.process(Map.of("text", "text")));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${text?contains('ex')},test: yes",
            "test: ${text?contains('EX')},test: no",
            "test: ${text?endsWith('xt')},test: yes",
            "test: ${text?ends_with('XT')},test: no",
            "test: ${text?startsWith('te')},test: yes",
            "test: ${text?starts_with('TE')},test: no",
    })
    void interpolationContainsAndStartOrEndWith(String templateSource, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("text", "text")));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${text[2..3]},test: CD",
            "test: ${text[2..]},test: CDEF",
    })
    void interpolationSlices(String templateSource, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("text", "ABCDEF")));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${text?camelCase},kebab-case,test: kebabCase",
            "test: ${text?camelCase},SCREAMING-KEBAB-CASE, test: screamingKebabCase",
            "test: ${text?camelCase},snake_case,test: snakeCase",
            "test: ${text?camelCase},SCREAMING_SNAKE_CASE, test: screamingSnakeCase",
    })
    void camelCase(String templateSource, String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("text", input)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${text?capitalize},The Quick brown fox jumps Over the lazy Dog,test: The Quick Brown Fox Jumps Over The Lazy Dog",
            "test: ${text?uncapitalize},The Quick BROWN fox jumps Over the lazy Dog,test: the quick bROWN fox jumps over the lazy dog",
    })
    void capitalizeAndUncapitalize(String templateSource, String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("text", input)));
    }

    @ParameterizedTest
    @CsvSource({
            "snake_case,thisIsATest,test: this_is_atest",
            "snake_case,thisIsAnAsapTest, test: this_is_an_asap_test",
            "screaming_snake_case,thisIsATest,test: THIS_IS_ATEST",
            "screaming_snake_case,thisIsAnAsapTest, test: THIS_IS_AN_ASAP_TEST",
            "kebabCase,thisIsATest,test: this-is-atest",
            "kebabCase,thisIsAnAsapTest, test: this-is-an-asap-test",
    })
    void developerCases(String builtIn, String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${text?" + builtIn + "}");
        assertEquals(expected, template.process(Map.of("text", input)));
    }

    @ParameterizedTest
    @CsvSource({
            "a short summer,test: a-short-summer",
            "In der Wüste,test: in-der-wste",
    })
    void slugify(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${text?slugify}");
        assertEquals(expected, template.process(Map.of("text", input)));
    }

    @ParameterizedTest
    @CsvSource({
            "'',1<2,test: 1&lt;2",
            "?esc('HTML'),1<2,test: 1&lt;2",
            "?escape('HTML'),1<2,test: 1&lt;2",
            "?noEsc,1<2,test: 1<2",
            "?no_esc,1<2,test: 1<2",
            "?no_escape,1<2,test: 1<2",
    })
    void escape(String builtIn, String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.withOutputFormat("HTML").getTemplate("test", "test: ${text" + builtIn + "}");
        assertEquals(expected, template.process(Map.of("text", input)));
    }

    @ParameterizedTest
    @CsvSource({
            "languageCountryVariant?locale?language,test: de",
            "languageCountryVariant?locale?lang,test: de",
            "languageCountryVariant?locale?country,test: DE",
            "languageCountry?locale?language,test: de",
            "languageCountry?locale?lang,test: de",
            "languageCountry?locale?country,test: DE",
            "language?locale?language,test: de",
            "language?locale?lang,test: de",
    })
    void locale(String builtIn, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${" + builtIn + "}");
        assertEquals(expected, template.process(Map.of(
                "languageCountryVariant", "de_DE_BFE", "languageCountry", "de_DE", "language", "de")));
    }

    @Test
    void unsupportedStringOperation(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "test: ${'xxx'?i18n}");
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void i18nWithoutResource(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "test: ${'xxx'?i18n}");
        template.setResourceBundle("freshmarker");
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void i18nWithTwoParameters(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "test: ${'xxx'?i18n('first', 'second')}");
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void i18n(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "test: ${'period.months'?i18n}");
        template.setResourceBundle("freshmarker");
        Map<String, Object> model = Map.of();
        assertEquals("test: Monate", template.process(model));
    }

    @Test
    void i18nWithParameter(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "test: ${'period.months'?i18n('freshmarker')}");
        Map<String, Object> model = Map.of();
        assertEquals("test: Monate", template.process(model));
    }
}