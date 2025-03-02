package org.freshmarker.core.ftl;

import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.core.BuiltinHandlingFeature;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class BuiltInNullInterpolationTest {
    @Test
    void interpolateBuiltInWithNull(TemplateBuilder builder) {
        Template template = builder.getTemplate("empty", "${value?upper_case!'-'}");
        Map<String, Object> model = Map.of();
        assertThrows(UnsupportedBuiltInException.class, () -> template.process(model));
    }

    @Test
    void interpolateBuiltInIgnoringNull(TemplateBuilder builder) {
        builder = builder.with(BuiltinHandlingFeature.IGNORE_NULL);
        Template template = builder.getTemplate("empty", "${value?upper_case!'-'}");
        Map<String, Object> model = Map.of();
        assertEquals("-",  template.process(model));
    }

    @ParameterizedTest
    @CsvSource({
            "${value!!upper_case!'-'}",
            "${value->upper_case!'-'}",
            "${value→upper_case!'-'}",
            "${value!!upper_case!!lower_case!'-'}",
            "${value->upper_case->lower_case!'-'}",
            "${value→upper_case→lower_case!'-'}"
    })
    void interpolateBuiltPipeSyntax(String input, TemplateBuilder builder) {
        Template template = builder.getTemplate("empty", "${value!!upper_case!'-'}");
        Map<String, Object> model = Map.of();
        assertEquals("-",  template.process(model));
    }
}
