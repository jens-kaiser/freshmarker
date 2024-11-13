package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.model.AbstractLimitedRange;
import org.freshmarker.core.model.TemplateLengthLimitedRange;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateRightLimitedRange;
import org.freshmarker.core.model.TemplateRightUnlimitedRange;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.List;
import java.util.Map;

import static de.schegge.collector.EnumeratedCollector.enumerated;

public final class SequencePluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateListSequence> BUILDER = new BuiltInKeyBuilder<>(TemplateListSequence.class);
    private static final BuiltInKeyBuilder<TemplateRightUnlimitedRange> UNLIMITED = new BuiltInKeyBuilder<>(TemplateRightUnlimitedRange.class);
    private static final BuiltInKeyBuilder<TemplateRightLimitedRange> LIMITED = new BuiltInKeyBuilder<>(TemplateRightLimitedRange.class);
    private static final BuiltInKeyBuilder<TemplateLengthLimitedRange> LENGTH = new BuiltInKeyBuilder<>(TemplateLengthLimitedRange.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(BUILDER.of("size"), (x, y, e) -> TemplateNumber.of(((TemplateListSequence) x).size(e)));
        builtIns.put(BUILDER.of("first"), (x, y, e) -> first((TemplateListSequence) x, e));
        builtIns.put(BUILDER.of("last"), (x, y, e) -> last((TemplateListSequence) x, e));
        builtIns.put(BUILDER.of("reverse"), (x, y, e) -> reverse((TemplateListSequence) x, e));
        builtIns.put(BUILDER.of("join"), (x, y, e) -> join(y, e, ((TemplateListSequence) x).getSequence(e)));
        builtIns.put(LIMITED.of("size"), (x, y, e) -> TemplateNumber.of(((AbstractLimitedRange) x).size(e)));
        builtIns.put(LIMITED.of("lower"), (x, y, e) -> ((AbstractLimitedRange) x).getLower());
        builtIns.put(LIMITED.of("upper"), (x, y, e) -> ((AbstractLimitedRange) x).getUpper(e));
        builtIns.put(LIMITED.of("reverse"), (x, y, e) -> ((TemplateRightLimitedRange) x).reverse(e));
        builtIns.put(LIMITED.of("join"), (x, y, e) -> join(y, e, ((AbstractLimitedRange) x).getSequence(e)));
        builtIns.put(LENGTH.of("size"), (x, y, e) -> TemplateNumber.of(((TemplateLengthLimitedRange) x).size(e)));
        builtIns.put(LENGTH.of("lower"), (x, y, e) -> ((TemplateLengthLimitedRange) x).getLower());
        builtIns.put(LENGTH.of("upper"), (x, y, e) -> ((TemplateLengthLimitedRange) x).getUpper(e));
        builtIns.put(LENGTH.of("reverse"), (x, y, e) -> ((TemplateLengthLimitedRange) x).reverse(e));
        builtIns.put(LENGTH.of("join"), (x, y, e) -> join(y, e, ((TemplateLengthLimitedRange) x).getSequence(e)));
        builtIns.put(UNLIMITED.of("lower"), (x, y, e) -> ((TemplateRightUnlimitedRange) x).getLower());
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

    private static TemplateString join(List<TemplateObject> parameter, ProcessContext context, List<Object> list) {
        String delimiter = parameter.isEmpty() ? ", " : parameter.getFirst().evaluate(context, TemplateString.class).getValue();
        String lastDelimiter = parameter.size() == 2 ? parameter.get(1).evaluate(context, TemplateString.class).getValue() : delimiter;
        String joined = list.stream().map(String::valueOf).collect(enumerated(delimiter, lastDelimiter));
        return new TemplateString(joined);
    }
}
