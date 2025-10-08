package org.freshmarker;

import org.freshmarker.core.ProcessException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TemplateTest {
    public static class Bean {
        private final String title;

        public Bean(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    public record Record(String title) {
    }

    @Nested
    class WithMap {
        private final Map<String, Object> dataModel = Map.of("name", "Jens", "bean", new Bean("Title"));

        private Template template;

        @BeforeEach
        void setUp(TemplateBuilder builder) {
            template = builder.getTemplate("example", "<#brick 'title'>title: ${bean.title}</#brick>");
        }

        @Test
        void process() {
            assertEquals("title: Title", template.process(dataModel));
        }

        @Test
        void processWithWriter() {
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            assertEquals("title: Title", writer.toString());
        }

        @Test
        void processBrick() {
            assertEquals("title: Title", template.processBrick("title", dataModel));
        }

        @Test
        void processBrickWithWriter() {
            StringWriter writer = new StringWriter();
            template.processBrick("title", dataModel, writer);
            assertEquals("title: Title", writer.toString());
        }
    }

    @Nested
    class WithBean {
        private Template template;

        @BeforeEach
        void setUp(TemplateBuilder builder) {
            template = builder.getTemplate("example", "<#brick 'title'>title: ${title}</#brick>");
        }

        @Test
        void process() {
            assertEquals("title: Title", template.process(new Bean("Title")));
        }

        @Test
        void processWithWriter() {
            StringWriter writer = new StringWriter();
            template.process(new Bean("Title"), writer);
            assertEquals("title: Title", writer.toString());
        }

        @Test
        void processBrick() {
            assertEquals("title: Title", template.processBrick("title", new Bean("Title")));
        }

        @Test
        void processBrickWithWriter() {
            StringWriter writer = new StringWriter();
            template.processBrick("title", new Bean("Title"), writer);
            assertEquals("title: Title", writer.toString());
        }
    }

    @Nested
    class WithRecord {
        private Template template;

        @BeforeEach
        void setUp(TemplateBuilder builder) {
            template = builder.getTemplate("example", "<#brick 'title'>title: ${title}</#brick>");
        }

        @Test
        void process() {
            assertEquals("title: Title", template.process(new Record("Title")));
        }

        @Test
        void processWithWriter() {
            StringWriter writer = new StringWriter();
            template.process(new Record("Title"), writer);
            assertEquals("title: Title", writer.toString());
        }

        @Test
        void processBrick() {
            assertEquals("title: Title", template.processBrick("title", new Record("Title")));
        }

        @Test
        void processBrickWithWriter() {
            StringWriter writer = new StringWriter();
            template.processBrick("title", new Record("Title"), writer);
            assertEquals("title: Title", writer.toString());
        }
    }

    @Test
    void processBrickWithWriter(TemplateBuilder builder) {
        Template template = builder.getTemplate("example", "<#brick 'title'>title: ${title}</#brick>");
        ProcessException exception = assertThrows(ProcessException.class, () -> template.process("test"));
        assertEquals("invalid data model: class java.lang.String", exception.getMessage());
    }
}