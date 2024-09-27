package org.freshmarker.core.ftl;

import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequenceBuiltInTest {

    private TemplateBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new Configuration().builder();
    }

    @Test
    void size() {
        assertEquals("7", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?size}").process(Map.of()));
    }

    @Test
    void first() {
        assertEquals("1", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?first}").process(Map.of()));
    }

    @Test
    void last() {
        assertEquals("64", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?last}").process(Map.of()));
    }

    @Test
    void reverse() {
        assertEquals("1", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?reverse?last}").process(Map.of()));
        assertEquals("64", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?reverse?first}").process(Map.of()));
    }

    @Test
    void join() {
        assertEquals("1, 2, 4, 8, 16, 32, 64", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?join}").process(Map.of()));
        assertEquals("1;2;4;8;16;32;64", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?join(';')}").process(Map.of()));
    }
}
