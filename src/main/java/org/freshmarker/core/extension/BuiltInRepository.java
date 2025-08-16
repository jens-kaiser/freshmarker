package org.freshmarker.core.extension;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.plugin.BuiltInHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class BuiltInRepository {
    private static final List<String> TYPE_CHECK_BUILT_INS = List.of("is_null", "is_string", "is_boolean", "is_number", "is_hash", "is_sequence",
            "is_enum", "is_range", "is_temporal", "is_character");

    private final Map<BuiltInKey, BuiltIn> builtIns;
    private final Map<String, Entry<BuiltInKey, BuiltIn>> hookableBuiltIns = new HashMap<>();

    public BuiltInRepository(Map<BuiltInKey, BuiltIn> builtIns) {
        this.builtIns = builtIns;
        Map<String, List<Entry<BuiltInKey, BuiltIn>>> collect = builtIns.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().getName()));
        for (Entry<String, List<Entry<BuiltInKey, BuiltIn>>> entry : collect.entrySet()) {
            if (entry.getValue().size() == 1) {
                hookableBuiltIns.put(entry.getKey(), entry.getValue().getFirst());
            }
        }
    }

    public Optional<Entry<BuiltInKey, BuiltIn>> byName(String builtInName) {
        if (!TYPE_CHECK_BUILT_INS.contains(builtInName)) {
            return Optional.ofNullable(hookableBuiltIns.get(builtInName));
        }
        return Optional.empty();
    }

    public BuiltIn byKey(Class<? extends TemplateObject> type, String name) {
        BuiltInKey builtInKey = new BuiltInKey(type, name);
        BuiltIn builtIn = builtIns.get(builtInKey);
        if (builtIn != null) {
            return builtIn;
        }
        if (TYPE_CHECK_BUILT_INS.contains(name)) {
            return BuiltInHelper.alwaysFalse();
        }
        throw new UnsupportedBuiltInException("unsupported builtin '" + name + "' for " + type.getSimpleName());
    }
}
