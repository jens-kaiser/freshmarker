package org.freshmarker.core.ftl;

import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TypeCheckBuiltInTest {
    @ParameterizedTest
    @CsvSource({
            "${null?is_null}",
            "${var?is_null}"
    })
    void checkNull(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_null", input);
        assertEquals("yes", template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource({
            "${string?is_null}",
            "${boolean?is_null}",
            "${number?is_null}"
    })
    void checkNotNull(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_null", input);
        Map<String, Object> model = Map.of("string", "", "boolean", true, "number", 42);
        assertEquals("no", template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "${'test'?is_string}",
            "${var?is_string}"
    })
    void checkString(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_string", input);
        assertEquals("yes", template.process(Map.of("var", "test")));
    }

    @ParameterizedTest
    @CsvSource({
            "${string?is_string}",
            "${boolean?is_string}",
            "${number?is_string}"
    })
    void checkNotString(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_string", input);
        Map<String, Object> model = Map.of( "boolean", true, "number", 42);
        assertEquals("no", template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "${true?is_boolean}",
            "${var?is_boolean}"
    })
    void checkBoolean(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_boolean", input);
        assertEquals("yes", template.process(Map.of("var", false)));
    }

    @ParameterizedTest
    @CsvSource({
            "${string?is_boolean}",
            "${null?is_boolean}",
            "${number?is_boolean}"
    })
    void checkNotBoolean(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_boolean", input);
        Map<String, Object> model = Map.of( "string", "test", "number", 42);
        assertEquals("no", template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "${42?is_number}",
            "${var?is_number}"
    })
    void checkNumber(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_number", input);
        assertEquals("yes", template.process(Map.of("var", 42)));
    }

    @ParameterizedTest
    @CsvSource({
            "${string?is_number}",
            "${null?is_number}",
            "${boolean?is_number}"
    })
    void checkNotNumber(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_number", input);
        Map<String, Object> model = Map.of( "string", "test", "boolean", true);
        assertEquals("no", template.process(model));
    }
}
