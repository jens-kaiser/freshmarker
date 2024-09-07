package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.Map;

public class SequencePluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateListSequence> BUILDER = new BuiltInKeyBuilder<>(TemplateListSequence.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(BUILDER.of("size"), (x, y, e) -> TemplateNumber.of(((TemplateListSequence) x).size(e)));
        builtIns.put(BUILDER.of("first"), (x, y, e) -> first((TemplateListSequence) x, e));
        builtIns.put(BUILDER.of("last"), (x, y, e) -> last((TemplateListSequence) x, e));
        builtIns.put(BUILDER.of("reverse"), (x, y, e) -> reverse((TemplateListSequence) x, e));
    }

    private static TemplateObject first(TemplateListSequence value, ProcessContext context) {
        return value.get(context, 0);
    }

    private static TemplateObject last(TemplateListSequence value, ProcessContext context) {
        return value.get(context, value.size(context) - 1);
    }

    private static TemplateListSequence reverse(TemplateListSequence value, ProcessContext context) {
        return new TemplateListSequence(value.getSequence(context).reversed());
    }
}
