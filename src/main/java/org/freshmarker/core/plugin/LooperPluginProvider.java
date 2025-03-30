package org.freshmarker.core.plugin;

import org.freshmarker.api.BuiltInProvider;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequenceLooper;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.utils.RomanNumbers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LooperPluginProvider implements BuiltInProvider {

    private static final List<TemplateObject> ITEM_PARITY = List.of(new TemplateString("odd"), new TemplateString("even"));
    private static final List<TemplateObject> ITEM_PARITY_CAP = List.of(new TemplateString("Odd"), new TemplateString("Even"));

    private static final BuiltInKeyBuilder<TemplateHashLooper> HASH = new BuiltInKeyBuilder<>(TemplateHashLooper.class);
    private static final BuiltInKeyBuilder<TemplateSequenceLooper> SEQUENCE = new BuiltInKeyBuilder<>(TemplateSequenceLooper.class);

    private void add(Map<BuiltInKey, BuiltIn> builtIns, String name, BuiltIn builtIn) {
        builtIns.put(HASH.of(name), builtIn);
        builtIns.put(SEQUENCE.of(name), builtIn);
    }

    @Override
    public Map<BuiltInKey, BuiltIn> provideBuiltIns() {
        Map<BuiltInKey, BuiltIn> builtIns = new HashMap<>();
        add(builtIns, "counter", (x, y, e) -> ((TemplateLooper) x).getCounter());
        add(builtIns, "item_parity", (x, y, e) -> ((TemplateLooper) x).cycle(ITEM_PARITY));
        add(builtIns, "index", (x, y, e) -> ((TemplateLooper) x).getIndex());
        add(builtIns, "roman", (x, y, e) -> RomanNumbers.roman(((TemplateLooper) x).getCounter()));
        add(builtIns, "utf_roman", (x, y, e) -> RomanNumbers.utfRoman(((TemplateLooper) x).getCounter()));
        add(builtIns, "clock_roman", (x, y, e) -> RomanNumbers.clockRoman(((TemplateLooper) x).getCounter()));
        add(builtIns, "is_first", (x, y, e) -> ((TemplateLooper) x).isFirst());
        add(builtIns, "is_last", (x, y, e) -> ((TemplateLooper) x).isLast());
        add(builtIns, "item_parity_cap", (x, y, e) -> ((TemplateLooper) x).cycle(ITEM_PARITY_CAP));
        add(builtIns, "item_cycle", (x, y, e) -> ((TemplateLooper) x).cycle(y));
        add(builtIns, "has_next", (x, y, e) -> ((TemplateLooper) x).hasNext());
        return builtIns;
    }
}
