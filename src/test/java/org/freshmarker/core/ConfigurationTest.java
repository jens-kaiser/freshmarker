package org.freshmarker.core;

import com.google.common.jimfs.Jimfs;
import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.FileSystemTemplateLoader;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigurationTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @Test
    void getStringTemplate() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${temporal}");
        assertNotNull(template);
        String result = template.process(Map.of("temporal", LocalTime.of(12, 30)));
        assertEquals("test: 12:30:00", result);
    }

    @Test
    void getPathTemplate() throws IOException {
        try (FileSystem fileSystem = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix())) {
            Path path = fileSystem.getPath("test.fmt");
            Files.writeString(path, "test: ${temporal}");
            Template template = configuration.getTemplate(path);
            assertNotNull(template);
            String result = template.process(Map.of("temporal", LocalTime.of(12, 30)));
            assertEquals("test: 12:30:00", result);
        }
    }

    @Test
    void getPathTemplateWithCharset() throws IOException {
        try (FileSystem fileSystem = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix())) {
            Path path = fileSystem.getPath("test.fmt");
            Files.writeString(path, "test: ${temporal}");
            Template template = configuration.getTemplate(path, StandardCharsets.UTF_8);
            assertNotNull(template);
            String result = template.process(Map.of("temporal", LocalTime.of(12, 30)));
            assertEquals("test: 12:30:00", result);
        }
    }
}