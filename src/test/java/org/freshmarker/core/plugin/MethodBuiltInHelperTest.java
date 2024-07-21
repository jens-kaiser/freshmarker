package org.freshmarker.core.plugin;

import org.freshmarker.core.ConfigurationException;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.TemplateObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MethodBuiltInHelperTest {
    private static class NotStaticTestPluginProvider implements PluginProvider {
        @BuiltInMethod
        public TemplateObject notStatic() {
            return null;
        }
    }

    private static class NoParameterTestPluginProvider implements PluginProvider {
        @BuiltInMethod
        public static  TemplateObject noParameter() {
            return null;
        }
    }

    private static class WrongReturnTypeTestPluginProvider implements PluginProvider {
        @BuiltInMethod
        public static String noParameter(TemplateObject object) {
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

    @Test
    void registerNoParameterBuiltIns() {
        PluginProvider provider = new NoParameterTestPluginProvider();
        Map<BuiltInKey, BuiltIn> builtIns = Map.of();
        ConfigurationException exception = assertThrows(ConfigurationException.class, () -> new MethodBuiltInHelper().registerBuiltIns(provider, builtIns));
        assertEquals("builtin method must have parameter", exception.getMessage());
    }

    @Test
    void registerWrongReturnTypeBuiltIns() {
        PluginProvider provider = new WrongReturnTypeTestPluginProvider();
        Map<BuiltInKey, BuiltIn> builtIns = Map.of();
        ConfigurationException exception = assertThrows(ConfigurationException.class, () -> new MethodBuiltInHelper().registerBuiltIns(provider, builtIns));
        assertEquals("result must be assignable from TemplateObject", exception.getMessage());
    }
}