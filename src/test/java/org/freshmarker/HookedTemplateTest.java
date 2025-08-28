package org.freshmarker;

import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(TemplateBuilderParameterResolver.class)
public class HookedTemplateTest {
    public static class Bean {
        private final String title;

        public Bean(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    @Test
    void hook(TemplateBuilder builder) {
        Map<String, Object> dataModel = Map.of("name", "Jens", "bean", new Bean("Title"));
        Template template = builder.getTemplate("example", "Name: ${name}, Bean: ${bean.title}").hook(dataModel);
        Assertions.assertEquals("Name: Jens, Bean: Title", template.process(dataModel));
    }
}
