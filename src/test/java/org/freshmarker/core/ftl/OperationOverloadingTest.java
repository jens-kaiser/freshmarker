package org.freshmarker.core.ftl;

import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class OperationOverloadingTest {
    @Nested
    class Sequences {
        @Test
        void add(TemplateBuilder builder) {
            Template template = builder.getTemplate("add", "${([1, 2, 3, 4] + [5, 6, 7, 8])?join}");
            assertEquals("1, 2, 3, 4, 5, 6, 7, 8", template.process(Map.of()));
        }

        @Test
        void remove(TemplateBuilder builder) {
            Template template = builder.getTemplate("add", "${([1, 2, 3, 4, 5, 6] - [1, 3, 5])?join}");
            assertEquals("2, 4, 6", template.process(Map.of()));
        }
    }
}
