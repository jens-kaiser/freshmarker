package org.freshmarker.core.extension;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.api.extension.BuiltInVariableProvider;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuiltInVariableProviderTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.register((BuiltInVariableProvider) () -> Map.of("example", context -> new TemplateString("Example")));
    }

    @Test
    void builtInVariable() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${.example}");
        assertEquals("test: Example", template.process(Map.of("test", "test")));
    }
}