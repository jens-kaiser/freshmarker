package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringInterpolationTest {

    private static final String TEXT = "The lazy Dog jumps over the Quick brown Fox";

    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
        configuration.setOutputFormat("HTML");
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${text},test: The lazy Dog jumps over the Quick brown Fox",
            "test: ${text?upper_case},test: THE LAZY DOG JUMPS OVER THE QUICK BROWN FOX",
            "test: ${text?lower_case},test: the lazy dog jumps over the quick brown fox",
            "test: ${text?upper_case?lower_case},test: the lazy dog jumps over the quick brown fox"
    })
    void interpolationString(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("text", TEXT)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${a?boolean},test: yes",
            "test: ${b?boolean},test: no"
    })
    void interpolationBoolean(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("a", "true", "b", "false")));
    }

    @Test
    void invalidInterpolationBoolean() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${text?boolean}");
        assertThrows(ProcessException.class, () ->  template.process(Map.of("text", "gonzo")));
    }

    @Test
    void interpolationTrim() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${text?trim}");
        assertEquals("test: text", template.process(Map.of("text", "  text  ")));
    }

    @Test
    void interpolationLength() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${text?length} ${text?trim?length}");
        assertEquals("test: 8 4", template.process(Map.of("text", "  text  ")));
    }

    @Test
    void interpolationDynamicKey() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${text[2]} ${text[3]}");
        assertEquals("test: x t", template.process(Map.of("text", "text")));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${text?contains('ex')},test: yes",
            "test: ${text?contains('EX')},test: no",
            "test: ${text?endsWith('xt')},test: yes",
            "test: ${text?ends_with('XT')},test: no",
    })
    void interpolationContainsAndEndWith(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("text", "text")));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${text[2..3]},test: CD",
            "test: ${text[2..]},test: CDEF",
    })
    void interpolationSlices(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("text", "ABCDEF")));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${text?camelCase},kebab-case,test: kebabCase",
            "test: ${text?camelCase},SCREAMING-KEBAB-CASE, test: screamingKebabCase",
            "test: ${text?camelCase},snake_case,test: snakeCase",
            "test: ${text?camelCase},SCREAMING_SNAKE_CASE, test: screamingSnakeCase",
    })
    void camelCase(String templateSource, String input, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("text", input)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${text?capitalize},The Quick brown fox jumps Over the lazy Dog,test: The Quick Brown Fox Jumps Over The Lazy Dog",
    })
    void capitalize(String templateSource, String input, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
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
    void developerCases(String builtIn, String input, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${text?" + builtIn + "}");
        assertEquals(expected, template.process(Map.of("text", input)));
    }

    @ParameterizedTest
    @CsvSource({
            "'',1<2,test: 1&lt;2",
            "?esc('HTML'),1<2,test: 1&lt;2",
            "?noEsc,1<2,test: 1<2",
    })
    void escape(String builtIn, String input, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${text" + builtIn + "}");
        assertEquals(expected, template.process(Map.of("text", input)));
    }
}