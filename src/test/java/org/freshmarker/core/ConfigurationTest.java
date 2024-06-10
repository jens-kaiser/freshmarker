package org.freshmarker.core;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigurationTest {

    @Test
    void getStringTemplate() throws ParseException {
        Configuration configuration = new Configuration();
        Template template = configuration.getTemplate("test", "test: ${temporal}");
        assertNotNull(template);
        String result = template.process(Map.of("temporal", LocalTime.of(12, 30)));
        assertEquals("test: 12:30:00", result);
    }

    
    @Test
    void getReaderTemplate() throws ParseException {
        Configuration configuration = new Configuration();
        Template template = configuration.getTemplate("test", new StringReader("test: ${temporal}"));
        assertNotNull(template);
        String result = template.process(Map.of("temporal", LocalTime.of(12, 30)));
        assertEquals("test: 12:30:00", result);
    }
}