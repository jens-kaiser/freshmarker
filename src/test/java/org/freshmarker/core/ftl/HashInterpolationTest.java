package org.freshmarker.core.ftl;

import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class HashInterpolationTest {

    @Test
    void constantHashValues(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "${{'a': 'A', 'b': 'B', 'c': 'C'}.b}");
        assertEquals("B", template.process(Map.of()));
    }

    @Test
    void variableHashValues(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "${{'a': 'A', 'b': b, 'c': 'C'}.b}");
        assertEquals("BB", template.process(Map.of("b", "BB")));
    }

    @Test
    void hashHashValues(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "${{'hash': { 'a': 'A', 'b': 'B'}}.hash.b}");
        assertEquals("B", template.process(Map.of()));
    }

    @Test
    void hashWithStaticHashOperator(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "${hash['key']!'eulav'}");
        assertEquals("value", template.process(Map.of("hash", Map.of("key", "value"))));
        assertEquals("eulav", template.process(Map.of("hash", Map.of("yek", "value"))));
    }

    @Test
    void hashWithVariableHashOperator(TemplateBuilder templateBuilder) {
        Template template = templateBuilder.getTemplate("test", "${hash[key]}");
        assertEquals("value", template.process(Map.of("hash", Map.of("KEY", "value"), "key", "KEY")));
    }
}
