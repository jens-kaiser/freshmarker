package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class CharacterInterpolationTest {
    @ParameterizedTest
    @ValueSource(chars = { 'a', 'Z', '€', '❶', '◯' })
    void interpolation(Character character, TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${char}");
        assertEquals("test: " + character, template.process(Map.of("char", character)));
    }
}
