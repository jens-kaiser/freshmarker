package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class RecordInterpolationTest {

    public record TestRecord(String name, boolean active) {
    }

    @Test
    void generateWithBean(TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "${record.name} ${record.active}");
        assertEquals("Record Name yes", template.process(Map.of("record", new TestRecord("Record Name", true))));
    }

    @Test
    void generateWithUnknownBeanAttribute(TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", "${record.value} ${record.active}");
        Map<String, Object> data = Map.of("record", new TestRecord("Record Name", true));
        assertThrows(ProcessException.class, () -> template.process(data));
    }
}