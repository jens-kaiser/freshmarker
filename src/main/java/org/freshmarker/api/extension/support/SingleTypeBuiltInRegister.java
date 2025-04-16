package org.freshmarker.api.extension.support;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.extension.Register;
import org.freshmarker.core.model.TemplateObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SingleTypeBuiltInRegister implements Register<Class<? extends TemplateObject>, String, BuiltIn> {
    private final Class<? extends TemplateObject> type;

    private final Map<String, BuiltIn> register = new HashMap<>();

    public SingleTypeBuiltInRegister(Class<? extends TemplateObject> type) {
        this.type = type;
    }

    public void add(String name, BuiltIn value) {
        register.put(Objects.requireNonNull(name), Objects.requireNonNull(value));
    }

    public void add(String name, String alternative, BuiltIn value) {
        add(name, value);
        add(alternative, value);
    }

    @Override
    public void add(Class<? extends TemplateObject> type, String name, BuiltIn value) {
        if (type == this.type) {
            add(name, value);
        }
    }

    @Override
    public Map<Class<? extends TemplateObject>, Map<String, BuiltIn>> asMap() {
        return Map.of(type, Map.copyOf(register));
    }
}
