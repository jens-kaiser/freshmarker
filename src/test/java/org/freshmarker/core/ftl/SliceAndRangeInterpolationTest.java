package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SliceAndRangeInterpolationTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @Test
    void interpolationSliceInclusive() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${list[2..4][1]}");
        assertEquals("test: 4", template.process(Map.of("list", List.of(1, 2, 3, 4, 5, 6, 7))));
    }

    @Test
    void interpolationSliceRightUnbound() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${list[2..][1]}");
        assertEquals("test: 4", template.process(Map.of("list", List.of(1, 2, 3, 4, 5, 6, 7))));
    }

    @Test
    void interpolationRangeRightUnbound() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${(0..)[100]}");
        assertEquals("test: 100", template.process(Map.of()));
    }

    @Test
    void interpolationRange() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${(0..20)[10]}");
        assertEquals("test: 10", template.process(Map.of()));
    }
}