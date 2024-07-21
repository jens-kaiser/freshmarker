package org.freshmarker.core.plugin;

import org.freshmarker.core.ConfigurationException;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.TemplateObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MethodBuiltInHelperTest {
    private static class NotStaticTestPluginProvider implements PluginProvider {
        @BuiltInMethod
        public TemplateObject notStatic() {
            return null;
        }
    }

    @Test
    void registerNotStaticBuiltIns() {
        PluginProvider provider = new NotStaticTestPluginProvider();
        Map<BuiltInKey, BuiltIn> builtIns = Map.of();
        ConfigurationException exception = assertThrows(ConfigurationException.class, () -> new MethodBuiltInHelper().registerBuiltIns(provider, builtIns));
        assertEquals("builtin method must be static", exception.getMessage());
    }

    private static class NoParameterTestPluginProvider implements PluginProvider {
        @BuiltInMethod
        public static  TemplateObject noParameter() {
            return null;
        }
    }

    @Test
    void registerNoParameterBuiltIns() {
        PluginProvider provider = new NoParameterTestPluginProvider();
        Map<BuiltInKey, BuiltIn> builtIns = Map.of();
        ConfigurationException exception = assertThrows(ConfigurationException.class, () -> new MethodBuiltInHelper().registerBuiltIns(provider, builtIns));
        assertEquals("builtin method must have parameter", exception.getMessage());
    }

    private static class WrongReturnTypeTestPluginProvider implements PluginProvider {
        @BuiltInMethod
        public static String noParameter(TemplateObject object) {
            return null;
        }
    }

    @Test
    void registerWrongReturnTypeBuiltIns() {
        PluginProvider provider = new WrongReturnTypeTestPluginProvider();
        Map<BuiltInKey, BuiltIn> builtIns = Map.of();
        ConfigurationException exception = assertThrows(ConfigurationException.class, () -> new MethodBuiltInHelper().registerBuiltIns(provider, builtIns));
        assertEquals("result must be assignable from TemplateObject", exception.getMessage());
    }

    private static class TestPluginProvider implements PluginProvider {
        @BuiltInMethod
        public static TemplateObject withContext(TemplateObject object, ProcessContext context) {
            return null;
        }
        @BuiltInMethod
        public static TemplateObject withAdditionalParameter(TemplateObject object, TemplateObject parameter) {
            return null;
        }
        @BuiltInMethod
        public static TemplateObject withAdditionalVarArgParameter(TemplateObject object, ProcessContext context, TemplateObject... parameter) {
            return null;
        }
        @BuiltInMethod
        public static TemplateObject withAdditionalParameterAndContext(TemplateObject object, ProcessContext context, TemplateObject parameter) {
            return null;
        }
        @BuiltInMethod
        public static TemplateObject withAdditionalVarArgParameterAndContext(TemplateObject object, ProcessContext context, TemplateObject... parameter) {
            return null;
        }
    }

    @Test
    void registerBuiltIns() {
        PluginProvider provider = new TestPluginProvider();
        Map<BuiltInKey, BuiltIn> builtIns = new HashMap<>();
        assertDoesNotThrow(() -> new MethodBuiltInHelper().registerBuiltIns(provider, builtIns));
        assertEquals(10, builtIns.size());
    }
}