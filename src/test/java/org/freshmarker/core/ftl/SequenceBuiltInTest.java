package org.freshmarker.core.ftl;

import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class SequenceBuiltInTest {

    @Test
    void size(TemplateBuilder builder) {
        assertEquals("7", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?size}").process(Map.of()));
    }

    @Test
    void first(TemplateBuilder builder) {
        assertEquals("1", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?first}").process(Map.of()));
    }

    @Test
    void last(TemplateBuilder builder) {
        assertEquals("64", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?last}").process(Map.of()));
    }

    @Test
    void is_empty(TemplateBuilder builder) {
        assertEquals("yes no", builder.getTemplate("test", "${[]?is_empty} ${[1]?is_empty}").process(Map.of()));
    }

    @Test
    void reverse(TemplateBuilder builder) {
        assertEquals("1", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?reverse?last}").process(Map.of()));
        assertEquals("64", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?reverse?first}").process(Map.of()));
    }

    @Test
    void join(TemplateBuilder builder) {
        assertEquals("1, 2, 4, 8, 16, 32, 64", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?join}").process(Map.of()));
        assertEquals("1;2;4;8;16;32;64", builder.getTemplate("test", "${[1,2,4,8,16,32,64]?join(';')}").process(Map.of()));
    }
}
