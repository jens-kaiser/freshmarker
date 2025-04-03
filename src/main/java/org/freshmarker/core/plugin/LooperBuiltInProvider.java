package org.freshmarker.core.plugin;

import org.freshmarker.api.extension.BuiltIn;
import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.support.BuiltInRegister;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequenceLooper;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.utils.RomanNumbers;

import java.util.List;


public final class LooperBuiltInProvider implements BuiltInProvider {

    private static final List<TemplateObject> ITEM_PARITY = List.of(new TemplateString("odd"), new TemplateString("even"));
    private static final List<TemplateObject> ITEM_PARITY_CAP = List.of(new TemplateString("Odd"), new TemplateString("Even"));

    private void add(BuiltInRegister builtInRegister, String name, BuiltIn builtIn) {
        builtInRegister.add(TemplateHashLooper.class, name, builtIn);
        builtInRegister.add(TemplateSequenceLooper.class, name, builtIn);
    }

    @Override
    public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
        BuiltInRegister builtInRegister = new BuiltInRegister();
        add(builtInRegister, "counter", (x, y, e) -> ((TemplateLooper) x).getCounter());
        add(builtInRegister, "item_parity", (x, y, e) -> ((TemplateLooper) x).cycle(ITEM_PARITY));
        add(builtInRegister, "index", (x, y, e) -> ((TemplateLooper) x).getIndex());
        add(builtInRegister, "roman", (x, y, e) -> RomanNumbers.roman(((TemplateLooper) x).getCounter()));
        add(builtInRegister, "utf_roman", (x, y, e) -> RomanNumbers.utfRoman(((TemplateLooper) x).getCounter()));
        add(builtInRegister, "clock_roman", (x, y, e) -> RomanNumbers.clockRoman(((TemplateLooper) x).getCounter()));
        add(builtInRegister, "is_first", (x, y, e) -> ((TemplateLooper) x).isFirst());
        add(builtInRegister, "is_last", (x, y, e) -> ((TemplateLooper) x).isLast());
        add(builtInRegister, "item_parity_cap", (x, y, e) -> ((TemplateLooper) x).cycle(ITEM_PARITY_CAP));
        add(builtInRegister, "item_cycle", (x, y, e) -> ((TemplateLooper) x).cycle(y));
        add(builtInRegister, "has_next", (x, y, e) -> ((TemplateLooper) x).hasNext());
        return builtInRegister;
    }
}
