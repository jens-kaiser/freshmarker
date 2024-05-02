package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecordInterpolationTest {

    private Configuration configuration;

    public record TestRecord(String name, boolean active) {
    }

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
    }

    @Test
    void generateWithBean() throws ParseException {
        Template template = configuration.getTemplate("test", "${record.name} ${record.active}");
        assertEquals("Record Name yes", template.process(Map.of("record", new TestRecord("Record Name", true))));
    }

    @Test
    void generateWithUnknownBeanAttribute() throws ParseException {
        Template template = configuration.getTemplate("test", "${record.value} ${record.active}");
        Map<String, Object> data = Map.of("record", new TestRecord("Record Name", true));
        assertThrows(ProcessException.class, () -> template.process(data));
    }
}