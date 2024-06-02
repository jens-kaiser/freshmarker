package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.Map;

public class SequencePluginProvider implements PluginProvider {
    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
    }

    @BuiltInMethod("size")
    public static TemplateNumber size(TemplateListSequence value, ProcessContext context) {
        return value.size(context);
    }

    @BuiltInMethod("first")
    public static TemplateObject first(TemplateListSequence value, ProcessContext context) {
        return value.get(context, 0);
    }

    @BuiltInMethod("last")
    public static TemplateObject last(TemplateListSequence value, ProcessContext context) {
        return value.get(context, value.size(context).asInt() - 1);
    }

    @BuiltInMethod("reverse")
    public static TemplateListSequence reverse(TemplateListSequence value, ProcessContext context) {
        return new TemplateListSequence(value.getSequence(context).reversed());
    }
}
