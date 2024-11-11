package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class BooleanInterpolationTest {

    @ParameterizedTest
    @CsvSource({
            "test: ${true},test: yes",
            "test: ${false},test: no",
            "test: ${!false},test: yes",
            "test: ${!flag},test: no",
            "test: ${!(!flag)},test: yes",
    })
    void interpolationConstant(String templateSource, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("flag", true)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${true?h},test: wahr",
            "test: ${false?h},test: falsch",
            "test: <#setting locale=\"en-UK\">${true?h},test: true",
            "test: <#setting locale=\"en-US\">${false?h},test: false",
            "test: <#setting locale=\"fr\">${true?h},test: vrai",
            "test: <#setting locale=\"fr-FR\">${false?h},test: faux",
    })
    void interpolationHuman(String templateSource, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
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
    void interpolationBuildIn(String templateSource, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("var", true, "text", "test")));
    }

    @Test
    void interpolationNumericalThen(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "${100 + (x > y)?then(x, y)}");
        assertEquals("142", template.process(Map.of("var", true, "x", 42, "y", 23)));
    }
}