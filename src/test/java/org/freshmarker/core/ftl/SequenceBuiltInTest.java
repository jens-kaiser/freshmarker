package org.freshmarker.core.ftl;

import org.freshmarker.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequenceBuiltInTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @Test
    void size() {
        assertEquals("7", configuration.getTemplate("test", "${[1,2,4,8,16,32,64]?size}").process(Map.of()));
    }

    @Test
    void first() {
        assertEquals("1", configuration.getTemplate("test", "${[1,2,4,8,16,32,64]?first}").process(Map.of()));
    }

    @Test
    void last() {
        assertEquals("64", configuration.getTemplate("test", "${[1,2,4,8,16,32,64]?last}").process(Map.of()));
    }

    @Test
    void reverse() {
        assertEquals("1", configuration.getTemplate("test", "${[1,2,4,8,16,32,64]?reverse?last}").process(Map.of()));
        assertEquals("64", configuration.getTemplate("test", "${[1,2,4,8,16,32,64]?reverse?first}").process(Map.of()));
    }
}
