package org.freshmarker.core.ftl;

import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TypeCheckBuiltInTest {
    private final Map<String, Object> typeExampleModel = Map.ofEntries(
            Map.entry("string", "text"),
            Map.entry("boolean", true),
            Map.entry("number", 42),
            Map.entry("enum", StandardOpenOption.CREATE),
            Map.entry("sequence", List.of(1, 2, 3)),
            Map.entry("hash", Map.of()),
            Map.entry("instant", Instant.now()),
            Map.entry("zoned", ZonedDateTime.now()),
            Map.entry("datetime", LocalDateTime.now()),
            Map.entry("date", LocalDate.now()),
            Map.entry("time", LocalTime.now()),
            Map.entry("year", Year.now()),
            Map.entry("yearmonth", YearMonth.now()),
            Map.entry("monthday", MonthDay.now())
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
    @ValueSource(strings = { "string", "boolean", "number", "enum", "sequence", "hash", "(1..10)",
            "instant", "zoned", "datetime", "date", "time", "year", "yearmonth", "monthday",
    })
    void checkNotNull(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_null", "${" + input + "?is_null}");
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
    @ValueSource(strings = { "null", "boolean", "number", "enum", "sequence", "hash", "(1..10)",
            "instant", "zoned", "datetime", "date", "time", "year", "yearmonth", "monthday",
    })
    void checkNotString(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_string", "${" + input + "?is_string}");
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
    @ValueSource(strings = { "string", "null", "number", "enum", "sequence", "hash", "(1..10)",
            "instant", "zoned", "datetime", "date", "time", "year", "yearmonth", "monthday",
    })
    void checkNotBoolean(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_boolean", "${" + input + "?is_boolean}");
        assertEquals("no", template.process(typeExampleModel));
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
    @ValueSource(strings = { "string", "null", "boolean", "enum", "sequence", "hash", "(1..10)",
            "instant", "zoned", "datetime", "date", "time", "year", "yearmonth", "monthday",
    })
    void checkNotNumber(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_number", "${" + input + "?is_number}");
        Map<String, Object> model = Map.of( "string", "test", "boolean", true);
        assertEquals("no", template.process(model));
    }

    @Test
    void checkEnum(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_enum", "${var?is_enum}");
        assertEquals("yes", template.process(Map.of("var", StandardOpenOption.CREATE)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "null", "string", "boolean", "number", "sequence", "hash", "(1..10)",
            "instant", "zoned", "datetime", "date", "time", "year", "yearmonth", "monthday",
    })
    void checkNotEnum(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_enum", "${" + input + "?is_enum}");
        assertEquals("no", template.process(typeExampleModel));
    }


    @Test
    void checkSequence(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_sequence", "${var?is_sequence}");
        assertEquals("yes", template.process(Map.of("var", List.of())));
    }

    @ParameterizedTest
    @ValueSource(strings = { "null", "string", "boolean", "number", "enum", "hash", "(1..10)",
            "instant", "zoned", "datetime", "date", "time", "year", "yearmonth", "monthday",
    })
    void checkNotSequence(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_sequence", "${" + input + "?is_sequence}");
        assertEquals("no", template.process(typeExampleModel));
    }

    @Test
    void checkHash(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_hash", "${var?is_hash}");
        assertEquals("yes", template.process(Map.of("var", Map.of())));
    }

    @ParameterizedTest
    @ValueSource(strings = { "null", "string", "boolean", "number", "enum", "sequence", "(1..10)",
            "instant", "zoned", "datetime", "date", "time", "year", "yearmonth", "monthday",
    })
    void checkNotHash(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_hash", "${" + input + "?is_hash}");
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
    @ValueSource(strings = { "null", "string", "boolean", "number", "enum", "sequence", "hash",
            "instant", "zoned", "datetime", "date", "time", "year", "yearmonth", "monthday",
    })
    void checkNotRange(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_range", "${" + input + "?is_range}");
        assertEquals("no", template.process(typeExampleModel));
    }

    @ParameterizedTest
    @ValueSource(strings = { "instant", "zoned", "datetime", "date", "time", "year", "yearmonth", "monthday" })
    void checkTemporal(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_temporal", "${" + input + "?is_temporal}");
        assertEquals("yes", template.process(typeExampleModel));
    }

    @ParameterizedTest
    @ValueSource(strings = { "null", "string", "boolean", "number", "enum", "sequence", "hash", "(1..10)" })
    void checkNotTemporal(String input, TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("is_temporal", "${" + input + "?is_temporal}");
        assertEquals("no", template.process(typeExampleModel));
    }
}
