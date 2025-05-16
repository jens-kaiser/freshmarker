package org.freshmarker.core.ftl;

import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TypeCheckBuiltInTest {
    private final Map<String, Object> typeExampleModel = Map.of(
            "string", "text", "boolean", true, "number", 42,
            "enum", StandardOpenOption.CREATE, "sequence", List.of(1, 2, 3), "hash", Map.of()
    );

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
            "${number?is_null}",
            "${enum?is_null}",
            "${sequence?is_null}",
            "${hash?is_null}",
            "${(1..10)?is_null}"
    })
    void checkNotNull(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_null", input);
        assertEquals("no", template.process(typeExampleModel));
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
            "${null?is_string}",
            "${boolean?is_string}",
            "${number?is_string}",
            "${enum?is_string}",
            "${sequence?is_string}",
            "${hash?is_string}",
            "${(1..10)?is_string}"
    })
    void checkNotString(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_string", input);
        assertEquals("no", template.process(typeExampleModel));
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
            "${number?is_boolean}",
            "${enum?is_boolean}",
            "${sequence?is_boolean}",
            "${hash?is_boolean}",
            "${(1..10)?is_boolean}"
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
            "${boolean?is_number}",
            "${enum?is_number}",
            "${sequence?is_number}",
            "${hash?is_number}",
            "${(1..10)?is_number}"
    })
    void checkNotNumber(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_number", input);
        Map<String, Object> model = Map.of( "string", "test", "boolean", true);
        assertEquals("no", template.process(model));
    }

    @Test
    void checkEnum(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_enum", "${var?is_enum}");
        assertEquals("yes", template.process(Map.of("var", StandardOpenOption.CREATE)));
    }

    @ParameterizedTest
    @CsvSource({
            "${string?is_enum}",
            "${null?is_enum}",
            "${boolean?is_enum}",
            "${number?is_enum}",
            "${sequence?is_enum}",
            "${hash?is_enum}",
            "${(1..10)?is_enum}"
    })
    void checkNotEnum(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_number", input);
        assertEquals("no", template.process(typeExampleModel));
    }


    @Test
    void checkSequence(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_sequence", "${var?is_sequence}");
        assertEquals("yes", template.process(Map.of("var", List.of())));
    }

    @ParameterizedTest
    @CsvSource({
            "${string?is_sequence}",
            "${null?is_sequence}",
            "${boolean?is_sequence}",
            "${number?is_sequence}",
            "${enum?is_sequence}",
            "${hash?is_sequence}",
            "${(1..10)?is_sequence}"
    })
    void checkNotSequence(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_sequence", input);
        assertEquals("no", template.process(typeExampleModel));
    }

    @Test
    void checkHash(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_hash", "${var?is_hash}");
        assertEquals("yes", template.process(Map.of("var", Map.of())));
    }

    @ParameterizedTest
    @CsvSource({
            "${string?is_hash}",
            "${null?is_hash}",
            "${boolean?is_hash}",
            "${number?is_hash}",
            "${enum?is_hash}",
            "${sequence?is_hash}",
            "${(1..10)?is_hash}"
    })
    void checkNotHash(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_hash", input);
        assertEquals("no", template.process(typeExampleModel));
    }

    @ParameterizedTest
    @CsvSource({
            "${(1..42)?is_range}",
            "${(1..<42)?is_range}",
            "${(1..*42)?is_range}"
    })
    void checkRange(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_range", input);
        assertEquals("yes", template.process(Map.of("var", Map.of())));
    }

    @ParameterizedTest
    @CsvSource({
            "${string?is_range}",
            "${null?is_range}",
            "${boolean?is_range}",
            "${number?is_range}",
            "${enum?is_range}",
            "${sequence?is_range}",
            "${hash?is_range}"
    })
    void checkNotRange(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_hash", input);
        assertEquals("no", template.process(typeExampleModel));
    }
}
