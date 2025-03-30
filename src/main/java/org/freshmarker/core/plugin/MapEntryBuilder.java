package org.freshmarker.core.plugin;

import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;
import java.util.Map.Entry;

public class MapEntryBuilder<T extends TemplateObject> {
    private final BuiltInKeyBuilder<T> builtInKeyBuilder;

    public MapEntryBuilder(Class<T> type) {
        builtInKeyBuilder = new BuiltInKeyBuilder<>(type);
    }
    Entry<BuiltInKey, BuiltIn> entry(String name, BuiltIn builtIn) {
        return Map.entry(builtInKeyBuilder.of(name), builtIn);
    }
}
