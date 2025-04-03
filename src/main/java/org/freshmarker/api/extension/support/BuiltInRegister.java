package org.freshmarker.api.extension.support;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.extension.Register;
import org.freshmarker.core.model.TemplateObject;

import java.util.HashMap;
import java.util.Map;

public class BuiltInRegister implements Register<Class<? extends TemplateObject>,String, BuiltIn> {
    private final Map<Class<? extends TemplateObject>, Map<String, BuiltIn>> register = new HashMap<>();

    @Override
    public void add(Class<? extends TemplateObject> type, String name, BuiltIn value) {
        register.computeIfAbsent(type, k -> new HashMap<>()).put(name, value);
    }

    @Override
    public Iterable<Class<? extends TemplateObject>> types() {
        return register.keySet();
    }

    @Override
    public Map<String, BuiltIn> byType(Class<? extends TemplateObject> type) {
        return register.getOrDefault(type, new HashMap<>());
    }
}
