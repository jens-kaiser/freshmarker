package org.freshmarker.core.plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequenceLooper;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class LooperPluginProvider implements PluginProvider {

    private static final List<TemplateObject> ITEM_PARITYTY = List.of(new TemplateString("odd"), new TemplateString("even"));

    private static final List<TemplateObject> ITEM_PARITYTY_CAP = List.of(new TemplateString("Odd"), new TemplateString("Even"));

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
    }

    @BuiltInMethod
    public static TemplateNumber index(TemplateSequenceLooper value) {
        return value.getIndex();
    }

    @BuiltInMethod
    public static TemplateNumber index(TemplateHashLooper value) {
        return value.getIndex();
    }

    @BuiltInMethod
    public static TemplateNumber counter(TemplateSequenceLooper value) {
        return value.getCounter();
    }

    @BuiltInMethod("roman")
    public static TemplateString romanCounter(TemplateSequenceLooper value) {
        return NumberPluginProvider.roman(value.getCounter());
    }

    @BuiltInMethod("utf_roman")
    public static TemplateString utfRomanCounter(TemplateSequenceLooper value) {
        return NumberPluginProvider.utfRoman(value.getCounter());
    }

    @BuiltInMethod("clock_roman")
    public static TemplateString clockCounter(TemplateSequenceLooper value) {
        return NumberPluginProvider.clockRoman(value.getCounter());
    }

    @BuiltInMethod
    public static TemplateNumber counter(TemplateHashLooper value) {
        return value.getCounter();
    }

    @BuiltInMethod("roman")
    public static TemplateString romanCounter(TemplateHashLooper value) {
        return NumberPluginProvider.roman(value.getCounter());
    }

    @BuiltInMethod("utf_roman")
    public static TemplateString utfRomanCounter(TemplateHashLooper value) {
        return NumberPluginProvider.utfRoman(value.getCounter());
    }

    @BuiltInMethod("clock_roman")
    public static TemplateString clockCounter(TemplateHashLooper value) {
        return NumberPluginProvider.clockRoman(value.getCounter());
    }

    @BuiltInMethod
    public static TemplateBoolean isFirst(TemplateSequenceLooper value) {
        return value.isFirst();
    }

    @BuiltInMethod
    public static TemplateBoolean isFirst(TemplateHashLooper value) {
        return value.isFirst();
    }

    @BuiltInMethod
    public static TemplateBoolean isLast(TemplateSequenceLooper value) {
        return value.isLast();
    }

    @BuiltInMethod
    public static TemplateBoolean isLast(TemplateHashLooper value) {
        return value.isLast();
    }

    @BuiltInMethod
    public static TemplateString itemParity(TemplateSequenceLooper value) {
        return (TemplateString) value.cycle(ITEM_PARITYTY);
    }

    @BuiltInMethod
    public static TemplateString itemParity(TemplateHashLooper value) {
        return (TemplateString) value.cycle(ITEM_PARITYTY);
    }

    @BuiltInMethod
    public static TemplateString itemParityCap(TemplateSequenceLooper value) {
        return (TemplateString) value.cycle(ITEM_PARITYTY_CAP);
    }

    @BuiltInMethod
    public static TemplateString itemParityCap(TemplateHashLooper value) {
        return (TemplateString) value.cycle(ITEM_PARITYTY_CAP);
    }

    @BuiltInMethod
    public static TemplateObject itemCycle(TemplateSequenceLooper value, TemplateObject... cycle) {
        return value.cycle(Arrays.asList(cycle));
    }

    @BuiltInMethod
    public static TemplateObject itemCycle(TemplateHashLooper value, TemplateObject... cycle) {
        return value.cycle(Arrays.asList(cycle));
    }

    @BuiltInMethod
    public static TemplateBoolean hasNext(TemplateSequenceLooper value) {
        return value.hasNext();
    }
    @BuiltInMethod
    public static TemplateBoolean hasNext(TemplateHashLooper value) {
        return value.hasNext();
    }
}
