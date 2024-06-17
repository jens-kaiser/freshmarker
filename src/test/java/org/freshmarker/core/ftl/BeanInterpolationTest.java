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

class BeanInterpolationTest {

    private Configuration configuration;

    public static class TestBean {

        private final String name;
        private final String description;
        private final boolean active;

        TestBean(String name, String description, boolean active) {
            this.name = name;
            this.description = description;
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isActive() {
            return active;
        }
    }

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @Test
    void generateWithBean() throws ParseException {
        Template template = configuration.getTemplate("test", "${bean.name} ${bean.active}");
        assertEquals("Bean Name yes", template.process(Map.of("bean", new TestBean("Bean Name", "Bean Description", true))));
    }

    @Test
    void generateWithBeanList() throws ParseException {
        Template template = configuration.getTemplate("test", "<#list bean as key, value>${key} ${value}, </#list>");
        assertEquals("name Bean Name, active yes, description Bean Description, ", template.process(Map.of("bean", new TestBean("Bean Name", "Bean Description", true))));
    }

    @Test
    void generateWithUnknownBeanAttribute() throws ParseException {
        Template template = configuration.getTemplate("test", "${bean.value} ${bean.active}");
        Map<String, Object> data = Map.of("bean", new TestBean("Bean Name", "Bean Description", true));
        ProcessException processException = assertThrows(ProcessException.class, () -> template.process(data));
        assertEquals("null at test:1:1 '${bean.value}'", processException.getMessage());
    }

    @Test
    void invalidBeanAccess() throws ParseException {
        Template template = configuration.getTemplate("test", "${bean}");
        Map<String, Object> data = Map.of("bean", new TestBean("Bean Name", "Bean Description", true));
        ProcessException processException = assertThrows(ProcessException.class, () -> template.process(data));
        assertEquals("missing reduction detected. Unsupported primitive? class org.freshmarker.core.ftl.BeanInterpolationTest$TestBean at test:1:1 '${bean}'", processException.getMessage());
    }

    @Test
    void illegalBeanAccess() throws ParseException {
        Template template = configuration.getTemplate("test", "${bean}");
        Map<String, Object> data = Map.of("bean", Runtime.getRuntime());
        ProcessException processException = assertThrows(ProcessException.class, () -> template.process(data));
        assertEquals("unsupported system class: class java.lang.Runtime at test:1:1 '${bean}'", processException.getMessage());
    }
}