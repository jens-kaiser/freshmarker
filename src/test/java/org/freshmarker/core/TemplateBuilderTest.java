package org.freshmarker.core;

import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Test
    void withFixedClock() throws InterruptedException {
        Instant instant = ZonedDateTime.of(1968, 8, 24, 12, 30, 0, 0,ZoneOffset.UTC).toInstant();
        templateBuilder = templateBuilder.withClock(Clock.fixed(instant, ZoneOffset.UTC));
        assertEquals("1968-08-24 01:30:00 Europe/Berlin", templateBuilder.getTemplate("test", "${.now}").process(Map.of()));
        TimeUnit.SECONDS.sleep(2);
        assertEquals("1968-08-24 01:30:00 Europe/Berlin", templateBuilder.getTemplate("test", "${.now}").process(Map.of()));
    }
}
