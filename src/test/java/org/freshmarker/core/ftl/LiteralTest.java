package org.freshmarker.core.ftl;

import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class LiteralTest {

    @Nested
    class Hash {
        @Test
        void simpleHashLiteral(TemplateBuilder builder) {
            Template template = builder.getTemplate("test", "${{ 'key': 42 }.key}");
            assertEquals("42", template.process(Map.of()));
        }

        @Test
        void hashLiteralWithVariable(TemplateBuilder builder) {
            Template template = builder.getTemplate("test", "${{ 'key1': 42, 'key2': number }.key2}");
            assertEquals("42", template.process(Map.of("number", 42)));
        }

        @Test
        void invalidKeyInHashLiteral(TemplateBuilder builder) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> builder.getTemplate("test", "${{ key: 42 } }.key}"));
            assertEquals("key is not a string", exception.getMessage());
        }
    }

    @Nested
    class Sequence {
        @Test
        void invalidListLiteral(TemplateBuilder builder) {
            Template template = builder.getTemplate("test", "${[1,2,'3',4,[true]][2]}");
            assertEquals("3", template.process(Map.of()));
        }

        @Test
        void simpleListLiteral(TemplateBuilder builder) {
            Template template = builder.getTemplate("test", "${[1,2,'3',4,5<6][2]}");
            assertEquals("3", template.process(Map.of()));
        }

        @Test
        void simpleListLiteralWithoutComma(TemplateBuilder builder) {
            Template template = builder.getTemplate("test", "${[1  2 '3'  true 3 < 4][2]}");
            assertEquals("3", template.process(Map.of()));
        }
    }
}
