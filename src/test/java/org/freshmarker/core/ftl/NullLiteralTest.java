package org.freshmarker.core.ftl;

import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

class NullLiteralTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @CsvSource({
            "expected=${null!},expected=",
            "expected=${null??},expected=no"
    })
    void nullLiteralOperations(String input, String expected) {
        Template template = configuration.getTemplate("test", input);
        Assertions.assertEquals(expected, template.process(Map.of()));
    }

    @Test
    void nullLiteralInterpolation() {
        Template template = configuration.getTemplate("test", "${null}");
        Map<String, Object> model = Map.of();
        Assertions.assertThrows(ProcessException.class, () -> template.process(model));
    }
}
