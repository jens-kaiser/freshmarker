package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.core.ProcessException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class LocaleInterpolationTest {

    @Nested
    class WithBuiltIn {
        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateLanguage(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale?language}");
            assertEquals(locale.getLanguage(), template.process(Map.of("locale", locale)));
        }

        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateDisplayLanguage(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale?language_name}");
            assertEquals(locale.getDisplayLanguage(Locale.GERMANY), template.process(Map.of("locale", locale)));
        }

        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateCountry(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale?country}");
            assertEquals(locale.getCountry(), template.process(Map.of("locale", locale)));
        }

        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateDisplayCountry(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale?country_name}");
            assertEquals(locale.getDisplayCountry(Locale.GERMANY), template.process(Map.of("locale", locale)));
        }

        static Stream<Locale> localeProvider() {
            return Stream.of(Locale.GERMANY, Locale.UK, Locale.JAPAN);
        }
    }

    @Nested
    class WithDotOperator {
        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateLanguage(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale.language}");
            assertEquals(locale.getLanguage(), template.process(Map.of("locale", locale)));
        }

        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateDisplayLanguage(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale.language_name}");
            assertEquals(locale.getDisplayLanguage(Locale.GERMANY), template.process(Map.of("locale", locale)));
        }

        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateCountry(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale.country}");
            assertEquals(locale.getCountry(), template.process(Map.of("locale", locale)));
        }

        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateDisplayCountry(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale.country_name}");
            assertEquals(locale.getDisplayCountry(Locale.GERMANY), template.process(Map.of("locale", locale)));
        }

        static Stream<Locale> localeProvider() {
            return Stream.of(Locale.GERMANY, Locale.UK, Locale.JAPAN);
        }
    }

    @Nested
    class WithHashOperator {
        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateLanguage(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale['language']}");
            assertEquals(locale.getLanguage(), template.process(Map.of("locale", locale)));
        }

        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateDisplayLanguage(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale['language_name']}");
            assertEquals(locale.getDisplayLanguage(Locale.GERMANY), template.process(Map.of("locale", locale)));
        }

        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateCountry(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale['country']}");
            assertEquals(locale.getCountry(), template.process(Map.of("locale", locale)));
        }

        @ParameterizedTest
        @MethodSource("localeProvider")
        void interpolateDisplayCountry(Locale locale, TemplateBuilder builder) throws ParseException {
            Template template = builder.getTemplate("language", "${locale['country_name']}");
            assertEquals(locale.getDisplayCountry(Locale.GERMANY), template.process(Map.of("locale", locale)));
        }

        static Stream<Locale> localeProvider() {
            return Stream.of(Locale.GERMANY, Locale.UK, Locale.JAPAN);
        }
    }

    @Test
    void unknownAttribute(TemplateBuilder builder) {
        Template template = builder.getTemplate("test", "${locale?value}");
        Map<String, Object> dataModel = Map.of("locale", Locale.GERMANY);
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }
}