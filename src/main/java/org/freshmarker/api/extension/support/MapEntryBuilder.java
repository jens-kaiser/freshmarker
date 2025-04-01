package org.freshmarker.api.extension.support;

import org.freshmarker.api.extension.BuiltIn;
import org.freshmarker.api.extension.BuiltInKey;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;
import java.util.Map.Entry;

public class MapEntryBuilder<T extends TemplateObject> {
    private final BuiltInKeyBuilder<T> builtInKeyBuilder;

    public MapEntryBuilder(Class<T> type) {
        builtInKeyBuilder = new BuiltInKeyBuilder<>(type);
    }

    public Entry<BuiltInKey, BuiltIn> entry(String name, BuiltIn builtIn) {
        return Map.entry(builtInKeyBuilder.of(name), builtIn);
    }
}
