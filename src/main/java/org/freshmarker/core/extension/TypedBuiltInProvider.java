package org.freshmarker.core.extension;

import org.freshmarker.api.BuiltInProvider;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;
import java.util.Map.Entry;

public abstract class TypedBuiltInProvider<T extends TemplateObject> implements BuiltInProvider {
    private final Class<T> type;

    public TypedBuiltInProvider(Class<T> type) {
        this.type = type;
    }

    protected Entry<BuiltInKey, BuiltIn> entry(String name, BuiltIn builtIn) {
        return Map.entry(new BuiltInKey(type, name), builtIn);
    }
    }
