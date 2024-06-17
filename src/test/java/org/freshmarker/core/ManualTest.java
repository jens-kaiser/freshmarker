package org.freshmarker.core;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManualTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @Test
    void range() throws ParseException {
        Template template = configuration.getTemplate("test", """
                <#list 1..5 as s>
                ${s} * ${s} = ${s*s}
                </#list>
                """);
        assertEquals("""
                1 * 1 = 1
                2 * 2 = 4
                3 * 3 = 9
                4 * 4 = 16
                5 * 5 = 25
                """, template.process(Map.of("a", 3, "b", 1)));
    }

    @Test
    void variableRange() throws ParseException {
        Template template = configuration.getTemplate("test", """
                <#list a..b as s>
                ${s} * ${s} = ${s*s}
                </#list>
                """);
        assertEquals("""
                3 * 3 = 9
                2 * 2 = 4
                1 * 1 = 1
                """, template.process(Map.of("a", 3, "b", 1)));
    }
}
