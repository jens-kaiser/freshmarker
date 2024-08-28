package org.freshmarker.core.plugin;

import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequenceLooper;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.List;
import java.util.Map;

public class LooperPluginProvider implements PluginProvider {

    private static final List<TemplateObject> ITEM_PARITYTY = List.of(new TemplateString("odd"), new TemplateString("even"));
    private static final List<TemplateObject> ITEM_PARITYTY_CAP = List.of(new TemplateString("Odd"), new TemplateString("Even"));

    private static final BuiltInKeyBuilder<TemplateHashLooper> HASH = new BuiltInKeyBuilder<>(TemplateHashLooper.class);
    private static final BuiltInKeyBuilder<TemplateSequenceLooper> SEQUENCE = new BuiltInKeyBuilder<>(TemplateSequenceLooper.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(HASH.of("counter"), (x, y, e) -> ((TemplateHashLooper) x).getCounter());
        builtIns.put(SEQUENCE.of("counter"), (x, y, e) -> ((TemplateSequenceLooper) x).getCounter());
        builtIns.put(HASH.of("item_parity"), (x, y, e) -> ((TemplateHashLooper) x).cycle(ITEM_PARITYTY));
        builtIns.put(SEQUENCE.of("item_parity"), (x, y, e) -> ((TemplateSequenceLooper) x).cycle(ITEM_PARITYTY));
        builtIns.put(HASH.of("index"), (x, y, e) -> ((TemplateHashLooper) x).getIndex());
        builtIns.put(SEQUENCE.of("index"), (x, y, e) -> ((TemplateSequenceLooper) x).getIndex());
        builtIns.put(HASH.of("roman"), (x, y, e) -> NumberPluginProvider.roman(((TemplateHashLooper) x).getCounter()));
        builtIns.put(SEQUENCE.of("roman"), (x, y, e) -> NumberPluginProvider.roman(((TemplateSequenceLooper) x).getCounter()));
        builtIns.put(HASH.of("utf_roman"), (x, y, e) -> NumberPluginProvider.utfRoman(((TemplateHashLooper) x).getCounter()));
        builtIns.put(SEQUENCE.of("utf_roman"), (x, y, e) -> NumberPluginProvider.utfRoman(((TemplateSequenceLooper) x).getCounter()));
        builtIns.put(HASH.of("clock_roman"), (x, y, e) -> NumberPluginProvider.clockRoman(((TemplateHashLooper) x).getCounter()));
        builtIns.put(SEQUENCE.of("clock_roman"), (x, y, e) -> NumberPluginProvider.clockRoman(((TemplateSequenceLooper) x).getCounter()));
        builtIns.put(HASH.of("is_first"), (x, y, e) -> ((TemplateHashLooper) x).isFirst());
        builtIns.put(SEQUENCE.of("is_first"), (x, y, e) -> ((TemplateSequenceLooper) x).isFirst());
        builtIns.put(HASH.of("is_last"), (x, y, e) -> ((TemplateHashLooper) x).isLast());
        builtIns.put(SEQUENCE.of("is_last"), (x, y, e) -> ((TemplateSequenceLooper) x).isLast());
        builtIns.put(HASH.of("item_parity_cap"), (x, y, e) -> ((TemplateHashLooper) x).cycle(ITEM_PARITYTY_CAP));
        builtIns.put(SEQUENCE.of("item_parity_cap"), (x, y, e) -> ((TemplateSequenceLooper) x).cycle(ITEM_PARITYTY_CAP));
        builtIns.put(HASH.of("item_cycle"), (x, y, e) -> ((TemplateHashLooper) x).cycle(y));
        builtIns.put(SEQUENCE.of("item_cycle"), (x, y, e) -> ((TemplateSequenceLooper) x).cycle(y));
        builtIns.put(HASH.of("has_next"), (x, y, e) -> ((TemplateHashLooper) x).hasNext());
        builtIns.put(SEQUENCE.of("has_next"), (x, y, e) -> ((TemplateSequenceLooper) x).hasNext());
    }
}
