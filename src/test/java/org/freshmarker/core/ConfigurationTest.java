package org.freshmarker.core;

import com.google.common.jimfs.Jimfs;
import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
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
        Template template = configuration.builder().getTemplate("test", "test: ${temporal}");
        assertNotNull(template);
        String result = template.process(Map.of("temporal", LocalTime.of(12, 30)));
        assertEquals("test: 12:30:00", result);
    }

    @Test
    void getReaderTemplate() throws ParseException {
        Template template = configuration.builder().getTemplate("test", new StringReader("test: ${temporal}"));
        assertNotNull(template);
        String result = template.process(Map.of("temporal", LocalTime.of(12, 30)));
        assertEquals("test: 12:30:00", result);
    }

    @Test
    void getPathTemplate() throws IOException {
        try (FileSystem fileSystem = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix())) {
            Path path = fileSystem.getPath("test.fmt");
            Files.writeString(path, "test: ${temporal}");
            Template template = configuration.builder().getTemplate(path);
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
            Template template = configuration.builder().getTemplate(path, StandardCharsets.UTF_8);
            assertNotNull(template);
            String result = template.process(Map.of("temporal", LocalTime.of(12, 30)));
            assertEquals("test: 12:30:00", result);
        }
    }

    @Test
    void registerFormatter() {
        configuration.registerFormatter(TemplateLocalDate.class, new DateFormatter("dd. MM. yyyy"));
        Template template = configuration.builder().getTemplate("test", "test: ${temporal}");
        assertNotNull(template);
        String result = template.process(Map.of("temporal", LocalDate.of(1968, Month.AUGUST, 24)));
        assertEquals("test: 24. 08. 1968", result);
    }

    @ParameterizedTest
    @CsvSource({
            "zoned-date-time,(DD) yyyy - hh,test: (237) 1968 - 12 / 1968-08-24 12:30:00 / 1968-08-24 / 12:30:00",
            "date-time,(DD) yyyy - hh,test: 1968-08-24 12:30:00 Z / (237) 1968 - 12 / 1968-08-24 / 12:30:00",
            "date,dd. MM. yyyy,test: 1968-08-24 12:30:00 Z / 1968-08-24 12:30:00 / 24. 08. 1968 / 12:30:00",
            "time,HH:mm,test: 1968-08-24 12:30:00 Z / 1968-08-24 12:30:00 / 1968-08-24 / 12:30",
    })
    void registerFormatter(String type, String pattern, String expected) {
        configuration.registerFormatter(type, pattern);
        Template template = configuration.builder().withLocale(Locale.GERMANY).getTemplate("test", "test: ${zoned} / ${dateTime} / ${date} / ${time}");
        assertNotNull(template);
        String result = template.process(Map.of(
                "zoned", ZonedDateTime.of(LocalDate.of(1968, Month.AUGUST, 24), LocalTime.of(12, 30), ZoneOffset.UTC),
                "dateTime", LocalDateTime.of(1968, Month.AUGUST, 24, 12, 30),
                "date", LocalDate.of(1968, Month.AUGUST, 24),
                "time", LocalTime.of(12, 30)
        ));
        assertEquals(expected, result);
    }

    @Test
    void registerNumberFormatter() {
        configuration.registerNumberFormatter("###,###.0000");
        Template template = configuration.builder().withLocale(Locale.GERMANY).getTemplate("test", "test: ${number}");
        assertNotNull(template);
        String result = template.process(Map.of("number", 128000.5));
        assertEquals("test: 128.000,5000", result);
    }

    @Test
    void registerNumberFormatterByType() {
        configuration.registerFormatter("number", "###,###.000");
        Template template = configuration.builder().withLocale(Locale.GERMANY).getTemplate("test", "test: ${number}");
        assertNotNull(template);
        String result = template.process(Map.of("number", 128000.5));
        assertEquals("test: 128.000,500", result);
    }
}