package org.freshmarker.core;

import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateBuilderTest {
    TemplateBuilder templateBuilder;

    @BeforeEach
    void setUp() {
        templateBuilder = new Configuration().builder();
    }

    @Test
    void getTemplate() {
        assertEquals("test", templateBuilder.getTemplate("test", "${'test'}").process(Map.of()));
    }

    @Test
    void getTemplateWithPath() {
        assertEquals("test", templateBuilder.getTemplate(Path.of("."), "test", new StringReader("${'test'}")).process(Map.of()));
    }
}
