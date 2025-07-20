package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.SystemFeature;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.Collator;
import java.util.HashMap;
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

    @ParameterizedTest
    @CsvSource({
            "test:${text?trim},'  text  ',test:text",
            "test:${text?trim},'\u1680 text \u1680',test:\u1680 text \u1680",
            "test:${text?strip},'\u000B\t\n\f\r\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u001C\u001D\u001E\u001F',test:\u2007",
            "test:${text?strip},'\u1680 text \u1680',test:text",
            "test:${text?strip_leading},'\u1680 text \u1680',test:text \u1680",
            "test:${text?strip_trailing},'\u1680 text \u1680',test:\u1680 text",
            "test:${text?trim_to_null!'xxx'},'  text  ',test:text",
            "test:${text?trim_to_null!'xxx'},'    ',test:xxx",
            "test:${text?trim_to_null!'xxx'},,test:xxx",
            "test:${text?strip_to_null!'xxx'},'\u1680  text  \u1680',test:text",
            "test:${text?strip_to_null!'xxx'},'  \u1680  ',test:xxx",
            "test:${text?strip_to_null!'xxx'},,test:xxx",
            "test:${text?empty_to_null!'xxx'},'test',test:test",
            "test:${text?empty_to_null!'xxx'},'',test:xxx",
            "test:${text?empty_to_null!'xxx'},,test:xxx",
            "test:${text?blank_to_null!'xxx'},'test',test:test",
            "test:${text?blank_to_null!'xxx'},'    ',test:xxx",
            "test:${text?blank_to_null!'xxx'},,test:xxx",
    })
    void interpolationTrim(String input, String text, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", input);
        Map<String, Object> model = new HashMap<>();
        model.put("text", text);
        assertEquals(expected, template.process(model));
    }

    @Test
    void interpolationLength(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${text?length} ${text?trim?length}");
        assertEquals("test: 8 4", template.process(Map.of("text", "  text  ")));
    }

    @Test
    void interpolationDynamicKeyAsCharacter(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${text[2]} ${text[3]} ${text[2]?is_character} ${text[3]?is_character}");
        assertEquals("test: x t yes yes", template.process(Map.of("text", "text")));
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
            "test: ${text?capitalize},The Quick brown fox jumps Over the lazy Dog,test: The Quick Brown Fox Jumps Over The Lazy Dog",
            "test: ${text?uncapitalize},The Quick BROWN fox jumps Over the lazy Dog,test: the quick bROWN fox jumps over the lazy dog",
    })
    void camelCaseCapitalizeAndUncapitalize(String templateSource, String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
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
        Template template = templateBuilder.getTemplate("test", "test: ${'key2'?i18n('test')}");
        Map<String, Object> model = Map.of();
        assertEquals("test: Wert 2", template.process(model));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${'apple'?left_pad(8)};test:    apple",
            "test: ${'orange'?left_pad(8)};test:   orange",
            "test: ${'pineapple'?left_pad(8)};test: pineapple",
            "test: ${'apple'?left_pad(8, '')};test: apple",
            "test: ${'apple'?left_pad(8, '●')};test: ●●●apple",
            "test: ${'apple'?left_pad(8, '●○')};test: ●○●apple",
            "test: ${'apple'?left_pad(9, '●○')};test: ●○●○apple",
            "test: ${'apple'?left_pad(10, '●○')};test: ●○●○●apple",
            "test: ${'apple'?left_pad(11, '●○')};test: ●○●○●○apple",
            "test: ${'apple'?left_pad(11, '● ○')};test: ● ○● ○apple",

            "test: ${'apple'?right_pad(8)};test: apple   ",
            "test: ${'orange'?right_pad(8)};test: orange  ",
            "test: ${'pineapple'?right_pad(8)};test: pineapple",
            "test: ${'apple'?right_pad(8, '')};test: apple",
            "test: ${'apple'?right_pad(8, '●')};test: apple●●●",
            "test: ${'apple'?right_pad(8, '●○')};test: apple●○●",
            "test: ${'apple'?right_pad(9, '●○')};test: apple●○●○",
            "test: ${'apple'?right_pad(10, '●○')};test: apple●○●○●",
            "test: ${'apple'?right_pad(11, '●○')};test: apple●○●○●○",
            "test: ${'apple'?right_pad(11, '● ○')};test: apple● ○● ○",

            "test: ${'apple'?center_pad(8)};test:   apple ",
            "test: ${'orange'?center_pad(8)};test:  orange ",
            "test: ${'pineapple'?center_pad(8)};test: pineapple",
            "test: ${'apple'?center_pad(8, '')};test: apple",
            "test: ${'apple'?center_pad(8, '●')};test: ●●apple●",
            "test: ${'apple'?center_pad(8, '●○')};test: ●○apple○",
            "test: ${'apple'?center_pad(9, '●○')};test: ●○apple○●",
            "test: ${'apple'?center_pad(10, '123')};test: 123apple31",
            "test: ${'apple'?center_pad(11, '123')};test: 123apple312",
            "test: ${'orange'?center_pad(11, '123')};test: 123orange12",
    }, delimiterString = ";", ignoreLeadingAndTrailingWhitespace = false)
    void padding(String input, String expected, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("padding", input);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${'apple'?left_pad}",
            "test: ${'orange'?left_pad('8')}",
            "test: ${'apple'?left_pad(8, 8)}",
            "test: ${'pinapple'?left_pad(8, '●', true)}",
            "test: ${'apple'?right_pad}",
            "test: ${'orange'?right_pad('8')}",
            "test: ${'apple'?right_pad(8, 8)}",
            "test: ${'pinapple'?right_pad(8, '●', true)}",
    }, delimiterString = ";", ignoreLeadingAndTrailingWhitespace = false)
    void invalidPadding(String input,TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("padding", input);
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${'secret'?mask};test: ******",
            "test: ${'5555 5555 5555 4444'?mask};test: **** **** **** ****",
            "test: ${'+49 176 04069042'?mask};test: *** *** ********",
            "test: ${'secret'?mask(1)};test: *****t",
            "test: ${'5555 5555 5555 4444'?mask(2)};test: **** **** **** **44",
            "test: ${'+49 176 04069042'?mask(2)};test: *** *** ******42",

            "test: ${'secret'?mask('●')};test: ●●●●●●",
            "test: ${'5555 5555 5555 4444'?mask('●')};test: ●●●● ●●●● ●●●● ●●●●",
            "test: ${'+49 176 04069042'?mask('●')};test: ●●● ●●● ●●●●●●●●",
            "test: ${'secret'?mask('●', 1)};test: ●●●●●t",
            "test: ${'5555 5555 5555 4444'?mask('●', 2)};test: ●●●● ●●●● ●●●● ●●44",
            "test: ${'+49 176 04069042'?mask('●', 2)};test: ●●● ●●● ●●●●●●42",

            "test: ${'secret'?mask('░▒▓')};test: ░▒▓░▒▓",
            "test: ${'5555 5555 5555 4444'?mask('░▒▓')};test: ░▒▓░ ▓░▒▓ ▒▓░▒ ░▒▓░",
            "test: ${'+49 176 04069042'?mask('░▒▓')};test: ░▒▓ ▒▓░ ▓░▒▓░▒▓░",
            "test: ${'secret'?mask('░▒▓', 1)};test: ░▒▓░▒t",
            "test: ${'5555 5555 5555 4444'?mask('░▒▓', 2)};test: ░▒▓░ ▓░▒▓ ▒▓░▒ ░▒44",
            "test: ${'+49 176 04069042'?mask('░▒▓', 2)};test: ░▒▓ ▒▓░ ▓░▒▓░▒42",
            "test: ${'secret'?mask(6)};test: secret",
            "test: ${'secret'?mask(10)};test: secret",

            "test: ${''?mask};test: ",
            "test: ${'secret'?mask('')};test: ******",
    }, delimiterString = ";", ignoreLeadingAndTrailingWhitespace = false)
    void mask(String input, String expected, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("mask", input);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${'secret'?mask_full};test: ******",
            "test: ${'5555 5555 5555 4444'?mask_full};test: *******************",
            "test: ${'+49 176 04069042'?mask_full};test: ****************",
            "test: ${'secret'?mask_full(1)};test: *****t",
            "test: ${'5555 5555 5555 4444'?mask_full(2)};test: *****************44",
            "test: ${'+49 176 04069042'?mask_full(2)};test: **************42",

            "test: ${'secret'?mask('●')};test: ●●●●●●",
            "test: ${'5555 5555 5555 4444'?mask_full('●')};test: ●●●●●●●●●●●●●●●●●●●",
            "test: ${'+49 176 04069042'?mask_full('●')};test: ●●●●●●●●●●●●●●●●",
            "test: ${'secret'?mask_full('●', 1)};test: ●●●●●t",
            "test: ${'5555 5555 5555 4444'?mask_full('●', 2)};test: ●●●●●●●●●●●●●●●●●44",
            "test: ${'+49 176 04069042'?mask_full('●', 2)};test: ●●●●●●●●●●●●●●42",

            "test: ${'secret'?mask_full('░▒▓')};test: ░▒▓░▒▓",
            "test: ${'5555 5555 5555 4444'?mask_full('░▒▓')};test: ░▒▓░▒▓░▒▓░▒▓░▒▓░▒▓░",
            "test: ${'+49 176 04069042'?mask_full('░▒▓')};test: ░▒▓░▒▓░▒▓░▒▓░▒▓░",
            "test: ${'secret'?mask_full('░▒▓', 1)};test: ░▒▓░▒t",
            "test: ${'5555 5555 5555 4444'?mask_full('░▒▓', 2)};test: ░▒▓░▒▓░▒▓░▒▓░▒▓░▒44",
            "test: ${'+49 176 04069042'?mask_full('░▒▓', 2)};test: ░▒▓░▒▓░▒▓░▒▓░▒42",

            "test: ${''?mask_full};test: ",
            "test: ${'secret'?mask_full('')};test: ******",
    }, delimiterString = ";", ignoreLeadingAndTrailingWhitespace = false)
    void mask_full(String input, String expected, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("mask_full", input);
        assertEquals(expected, template.process(Map.of()));
    }

    @Test
    void is_empty(TemplateBuilder builder) {
        assertEquals("yes no", builder.getTemplate("test", "${''?is_empty} ${'test'?is_empty}").process(Map.of()));
    }

    @Test
    void validOperation(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("valid", "${'Jens' + ' ' + 'Kaiser'}");
        assertEquals("Jens Kaiser", template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource({
            "'Jens' - ' ' - 'Kaiser',unsupported operation: MINUS at invalid:1:3",
            "'Jens' * ' ' * 'Kaiser',unsupported operation: TIMES at invalid:1:3"})
    void invalidOperation(String input, String message, TemplateBuilder templateBuilder) {
        ParsingException exception = assertThrows(ParsingException.class, () -> templateBuilder.getTemplate("invalid", "${" +  input + "}"));
        assertEquals(message + " '" + input + "'", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "text1 < text2,test: yes", "text1 <= text2,test: yes", "text1 ≤ text2,test: yes",
            "text2 > text1,test: yes", "text2 >= text1,test: yes", "text2 ≥ text1,test: yes",
            "text2 < text1,test: no", "text2 <= text1,test: no", "text2 ≤ text1,test: no",
            "text1 > text2,test: no", "text1 >= text2,test: no", "text1 ≥ text2,test: no"
    })
    void relation(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${" + input +"}");
        Map<String, Object> model = Map.of("text1", "Jens", "text2", "Kaiser");
        assertEquals(expected, template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "text1 < text2,test: yes", "text1 <= text2,test: yes", "text1 ≤ text2,test: yes",
            "text2 > text1,test: yes", "text2 >= text1,test: yes", "text2 ≥ text1,test: yes",
            "text2 < text1,test: no", "text2 <= text1,test: no", "text2 ≤ text1,test: no",
            "text1 > text2,test: no", "text1 >= text2,test: no", "text1 ≥ text2,test: no"
    })
    void relationWithoutCollator(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${" + input + "}");
        Map<String, Object> model = Map.of("text1", "JENS", "text2", "jens");
        assertEquals(expected, template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "text1 < text2,test: no", "text1 <= text2,test: yes", "text1 ≤ text2,test: yes",
            "text2 > text1,test: no", "text2 >= text1,test: yes", "text2 ≥ text1,test: yes",
            "text2 < text1,test: no", "text2 <= text1,test: yes", "text2 ≤ text1,test: yes",
            "text1 > text2,test: no", "text1 >= text2,test: yes", "text1 ≥ text2,test: yes"
    })
    void relationWithCollator(String input, String expected, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.with(SystemFeature.LOCALE_SENSITIVE_STRING_COMPARE, Collator.PRIMARY)
                .getTemplate("test", "test: ${" + input + "}");
        Map<String, Object> model = Map.of("text1", "jens", "text2", "JENS");
        assertEquals(expected, template.process(model));
    }
}