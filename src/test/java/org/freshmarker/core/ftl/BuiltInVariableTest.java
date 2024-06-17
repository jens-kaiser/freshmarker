package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuiltInVariableTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${.lang},test: de",
            "test: ${.locale},test: de_DE",
            "test: ${.country},test: DE",
            "test: ${.version},test: 1.0.0",
    })
    void builtInVariables(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @Test
    void now() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${.now?date}");
        assertEquals("test: " + LocalDate.now(), template.process(Map.of()));
    }

    @Test
    void unknownBuiltInVariable() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${.gonzo}");
        Map<String, Object> model = Map.of();
        assertThrows(IllegalStateException.class, () -> template.process(model));
    }
}