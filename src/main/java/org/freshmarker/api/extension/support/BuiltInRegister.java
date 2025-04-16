package org.freshmarker.api.extension.support;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.extension.Register;
import org.freshmarker.core.model.TemplateObject;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class BuiltInRegister implements Register<Class<? extends TemplateObject>, String, BuiltIn> {
    private final Map<Class<? extends TemplateObject>, Map<String, BuiltIn>> register = new HashMap<>();

    @Override
    public void add(Class<? extends TemplateObject> type, String name, BuiltIn value) {
        register.computeIfAbsent(requireNonNull(type), k -> new HashMap<>()).put(requireNonNull(name), requireNonNull(value));
    }

    @Override
    public Map<Class<? extends TemplateObject>, Map<String, BuiltIn>> asMap() {
        return Map.copyOf(register);
    }
}
