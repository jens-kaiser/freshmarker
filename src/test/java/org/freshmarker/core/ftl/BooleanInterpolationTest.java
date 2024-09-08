package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BooleanInterpolationTest {
    private TemplateBuilder templateBuilder;

    @BeforeEach
    void setUp() {
        Configuration configuration = new Configuration();
        templateBuilder = configuration.builder();
        templateBuilder.withLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${true},test: yes",
            "test: ${false},test: no",
            "test: ${!false},test: yes",
            "test: ${!flag},test: no",
    })
    void interpolationConstant(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("flag", true)));
    }

    @ParameterizedTest
    @CsvSource({
            "de,test: ${true?h},test: wahr",
            "de,test: ${false?h},test: falsch",
            "en,test: ${true?h},test: true",
            "en,test: ${false?h},test: false",
            "fr,test: ${true?h},test: vrai",
            "fr,test: ${false?h},test: faux",
    })
    void interpolationHuman(Locale locale, String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.withLocale(locale).getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${true?string('ja','nein')};test: ja",
            "test: ${false?string('ja','nein')};test: nein",
            "test: ${true?then(text,'nein')};test: test",
            "test: ${false?then('ja',text)};test: test",
            "test: ${var?then(text,'nein')};test: test",
            "test: ${(!var)?then('ja',text)};test: test",
    }, delimiterString = ";")
    void interpolationBuildIn(String templateSource, String expected) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("var", true, "text", "test")));
    }

    @Test
    void interpolationNumericalThen() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "${100 + (x > y)?then(x, y)}");
        assertEquals("142", template.process(Map.of("var", true, "x", 42, "y", 23)));
    }
}