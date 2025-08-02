package org.freshmarker.core.ftl;

import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class LiteralTest {

    @Nested
    class Hashes {
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
    class Sequences {
        @Test
        void listLiteralWithNonPrimitiveEntries(TemplateBuilder builder) {
            Template template = builder.getTemplate("test", "${[1,2,'3',4,[true]][2]}");
            assertEquals("3", template.process(Map.of()));
        }

        @Test
        void simpleListLiteral(TemplateBuilder builder) {
            Template template = builder.getTemplate("test", "${[1,2,'3',4,5<6][4]}");
            assertEquals("yes", template.process(Map.of()));
        }

        @Test
        void simpleListLiteralWithoutComma(TemplateBuilder builder) {
            Template template = builder.getTemplate("test", "${[1  2 '3'  true 3 < 4][2]}");
            assertEquals("3", template.process(Map.of()));
        }
    }

    @Nested
    class Numbers {
        @ParameterizedTest
        @ValueSource(longs = { Long.MIN_VALUE, Integer.MIN_VALUE - 1L, Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE, Integer.MAX_VALUE + 1L, Long.MAX_VALUE })
        void interpolateIntegerOrLongLiteral(long number, TemplateBuilder templateBuilder) {
            Template template = templateBuilder.getTemplate("test", "${"+ number + "}");
            assertDoesNotThrow(() -> template.process(Map.of()));
        }

        @ParameterizedTest
        @ValueSource(longs = { Long.MIN_VALUE, Integer.MIN_VALUE - 1L, Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE, Integer.MAX_VALUE + 1L, Long.MAX_VALUE })
        void interpolateLongLiteral(long number, TemplateBuilder templateBuilder) {
            Template template = templateBuilder.getTemplate("test", "${"+ number + "L}");
            assertDoesNotThrow(() -> template.process(Map.of()));
        }
    }
}
