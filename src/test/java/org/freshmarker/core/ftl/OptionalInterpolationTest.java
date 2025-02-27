package org.freshmarker.core.ftl;

import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class OptionalInterpolationTest {
    @Test
    void interpolateEmpty(TemplateBuilder builder) {
        assertEquals("-", builder.getTemplate("empty", "${optional!'-'}").process(Map.of("optional", Optional.empty())));
    }

    @Test
    void interpolatePresent(TemplateBuilder builder) {
        assertEquals("42", builder.getTemplate("optional", "${optional}").process(Map.of("optional", Optional.of(42))));
    }

    @Test
    void interpolateExists(TemplateBuilder builder) {
        Map<String, Object> emptyMap = Map.of("optional", Optional.empty());
        Map<String, Object> presentMap = Map.of("optional", Optional.of(42));
        assertEquals("no", builder.getTemplate("optional", "${optional??}").process(emptyMap));
        assertEquals("yes", builder.getTemplate("optional", "${optional == null}").process(emptyMap));
        assertEquals("no", builder.getTemplate("optional", "${optional != null}").process(emptyMap));
        assertEquals("yes", builder.getTemplate("optional", "${optional??}").process(presentMap));
        assertEquals("no", builder.getTemplate("optional", "${optional == null}").process(presentMap));
        assertEquals("yes", builder.getTemplate("optional", "${optional != null}").process(presentMap));
    }
}
