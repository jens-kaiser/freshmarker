package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class BeanInterpolationTest {

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

    @Test
    void generateWithBean(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "${bean.name} ${bean.active}");
        assertEquals("Bean Name yes", template.process(Map.of("bean", new TestBean("Bean Name", "Bean Description", true))));
    }

    @Test
    void generateWithBeanList(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "<#list bean as key sorted asc, value>${key} ${value}, </#list>\n" +
                                                                "<#list bean as key sorted desc, value>${key} ${value}, </#list>");
        assertEquals("active yes, description Bean Description, name Bean Name, \n" +
                     "name Bean Name, description Bean Description, active yes, ",
                template.process(Map.of("bean", new TestBean("Bean Name", "Bean Description", true))));
    }

    @Test
    void generateWithUnknownBeanAttribute(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "${bean.value} ${bean.active}");
        Map<String, Object> data = Map.of("bean", new TestBean("Bean Name", "Bean Description", true));
        ProcessException processException = assertThrows(ProcessException.class, () -> template.process(data));
        assertEquals("null at test:1:1 '${bean.value}'", processException.getMessage());
    }

    @Test
    void invalidBeanAccess(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "${bean}");
        Map<String, Object> data = Map.of("bean", new TestBean("Bean Name", "Bean Description", true));
        ProcessException processException = assertThrows(ProcessException.class, () -> template.process(data));
        assertEquals("missing reduction detected. Unsupported primitive? class org.freshmarker.core.ftl.BeanInterpolationTest$TestBean at test:1:1 '${bean}'", processException.getMessage());
    }
}