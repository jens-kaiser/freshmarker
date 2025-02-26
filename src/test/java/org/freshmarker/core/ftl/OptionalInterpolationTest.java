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
}
