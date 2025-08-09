package org.freshmarker.core.ftl;

import org.freshmarker.Configuration;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.SystemFeature;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SequenceBuiltInTest {

    @Nested
    @ExtendWith(TemplateBuilderParameterResolver.class)
    class ListSequences {
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

    @Nested
    class SetAsSequence {

        @Test
        void withError() {
            TemplateBuilder builder = new Configuration().builder();
            Map<String, Object> dataModel = Map.of("sequence", Set.of());
            assertThrows(ProcessException.class, () -> builder.getTemplate("test", "${sequence?join}").process(dataModel));
        }

        @Test
        void asSequence() {
            TemplateBuilder builder = new Configuration(SystemFeature.SET_AS_SEQUENCE).builder();
            Map<String, Object> dataModel = Map.of("sequence", Set.of(1, 11, 111, 1111));
            assertEquals("1111111111", builder.getTemplate("test", "${sequence?join('')}").process(dataModel));
        }

        @Test
        void sequencedSetAsSequence() {
            TemplateBuilder builder = new Configuration().builder();
            Map<String, Object> dataModel = Map.of("sequence", new LinkedHashSet<>(List.of(1, 2, 4, 8, 16, 32, 64)));
            assertEquals("1, 2, 4, 8, 16, 32, 64", builder.getTemplate("test", "${sequence?join}").process(dataModel));
        }
    }

    @Nested
    class CollectionAsSequence {
        @Test
        void withError() {
            TemplateBuilder builder = new Configuration().builder();
            Map<String, Object> dataModel = Map.of("sequence", Collections.unmodifiableCollection(new ArrayList<>()));
            assertThrows(ProcessException.class, () -> builder.getTemplate("test", "${sequence?join}").process(dataModel));
        }

        @Test
        void asSequence() {
            TemplateBuilder builder = new Configuration(SystemFeature.COLLECTION_AS_SEQUENCE).builder();
            Map<String, Object> dataModel = Map.of("sequence", Collections.unmodifiableCollection(new ArrayList<>(List.of(1, 2, 4, 8, 16, 32, 64))));
            assertEquals("1, 2, 4, 8, 16, 32, 64", builder.getTemplate("test", "${sequence?join}").process(dataModel));
        }
    }

    @Nested
    class ArrayAsSequence {

        @Test
        void asSequence() {
            TemplateBuilder builder = new Configuration().builder();
            Map<String, Object> dataModel = Map.of("sequence", new String[] { "1", "2", "4", "8", "16", "32", "64" });
            assertEquals("1, 2, 4, 8, 16, 32, 64", builder.getTemplate("test", "${sequence?join}").process(dataModel));
        }

        @Test
        void asSequenceFromPrimitive() {
            TemplateBuilder builder = new Configuration().builder();
            Map<String, Object> dataModel = Map.of("sequence", new int[] { 1, 2, 4, 8, 16, 32, 64 });
            assertEquals("1, 2, 4, 8, 16, 32, 64", builder.getTemplate("test", "${sequence?join}").process(dataModel));
        }
    }
}
