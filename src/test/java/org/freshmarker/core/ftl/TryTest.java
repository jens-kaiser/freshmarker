package org.freshmarker.core.ftl;

import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TryTest {
    @Test
    void tryWithoutException(TemplateBuilder builder) {
        Template template = builder.getTemplate("try", """
                <#try>
                This is a try test.
                <#except>
                This is a except test.
                </#try>
                """);
        Assertions.assertEquals("This is a try test.\n", template.process(Map.of()));
    }

    @Test
    void tryWithException(TemplateBuilder builder) {
        Template template = builder.getTemplate("try", """
                <#try>
                ${value}
                <#except>
                This is a except test.
                </#try>
                """);
        Assertions.assertEquals("This is a except test.\n", template.process(Map.of()));
    }

    @Test
    void reduceTryWithoutException(TemplateBuilder builder) {
        Template template = builder.getTemplate("try", """
                <#try>
                ${key} ${value}
                <#except>
                This is a except test.
                </#try>
                """);
        Template reduced = template.reduce(Map.of("key", "key"));
        Assertions.assertEquals("key value\n", reduced.process(Map.of("value", "value")));
    }

    @Test
    void reduceTryWithException(TemplateBuilder builder) {
        Template template = builder.getTemplate("try", """
                <#try>
                ${key} ${value}
                <#except>
                This is a except test.
                </#try>
                """);
        Template reduced = template.reduce(Map.of("key", "key"));
        Assertions.assertEquals("This is a except test.\n", reduced.process(Map.of()));
    }
}
