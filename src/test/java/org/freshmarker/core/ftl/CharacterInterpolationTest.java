package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class CharacterInterpolationTest {
    @ParameterizedTest
    @ValueSource(chars = { 'a', 'Z', '€', '❶', '◯' })
    void interpolation(Character character, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", "test: ${char} ${char?c}");
        assertEquals("test: " + character + " " + character, template.process(Map.of("char", character)));
    }

    @ParameterizedTest
    @CsvSource({ "a,BASIC_LATIN", "Z,BASIC_LATIN", "€,CURRENCY_SYMBOLS", "❶,DINGBATS", "◯,GEOMETRIC_SHAPES" })
    void interpolateUnicodeBlock(Character character, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("block", "test: ${char} ${char?unicode_block}");
        assertEquals("test: " + character + " " + expected, template.process(Map.of("char", character)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "a,no no yes no yes yes no", "Z,no no yes no yes no yes", "€,no no no no no no no", "❶,no no no no no no no",
            "◯,no no no no no no no", " ,yes no no no no no no"}, ignoreLeadingAndTrailingWhitespace = false)
    void interpolationIs(Character character, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("is",
                "${char?is_whitespace} ${char?is_digit} ${char?is_letter} ${char?is_emoji} " +
                        "${char?is_alphabetic} ${char?is_lower_case} ${char?is_upper_case}");
        assertEquals(expected, template.process(Map.of("char", character)));
    }

    @ParameterizedTest
    @CsvSource({"a,A a", "Z, Z z"})
    void interpolationConvert(Character character, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("is", "${char?upper_case} ${char?lower_case}");
        assertEquals(expected, template.process(Map.of("char", character)));
    }
}
