package org.freshmarker.core.plugin;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.support.BuiltInRegister;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.AbstractLimitedRange;
import org.freshmarker.core.model.TemplateLengthLimitedRange;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateRightLimitedRange;
import org.freshmarker.core.model.TemplateRightUnlimitedRange;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.List;

import static de.schegge.collector.EnumeratedCollector.enumerated;

public final class SequenceBuildInProvider implements BuiltInProvider {
    private static final String REVERSE = "reverse";
    private static final String JOIN = "join";
    private static final String LOWER = "lower";
    private static final String SIZE = "size";
    private static final String IS_RANGE = "is_range";

    private static TemplateObject first(TemplateListSequence value, ProcessContext context) {
        return value.get(context, 0);
    }

    private static TemplateObject last(TemplateListSequence value, ProcessContext context) {
        return value.get(context, value.size(context) - 1);
    }

    private static TemplateListSequence reverse(TemplateListSequence value) {
        return new TemplateListSequence(value.getSequence().reversed());
    }

    private static TemplateString join(List<TemplateObject> parameter, ProcessContext context, List<Object> list) {
        String delimiter = parameter.isEmpty() ? ", " : parameter.getFirst().evaluate(context, TemplateString.class).getValue();
        String lastDelimiter = parameter.size() == 2 ? parameter.get(1).evaluate(context, TemplateString.class).getValue() : delimiter;
        String joined = list.stream().map(String::valueOf).collect(enumerated(delimiter, lastDelimiter));
        return new TemplateString(joined);
    }

    @Override
    public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
        BuiltInRegister register = new BuiltInRegister();
        register.add(TemplateListSequence.class, SIZE, (x, y, e) -> TemplateNumber.of(((TemplateListSequence) x).size(e)));
        register.add(TemplateListSequence.class, "first", (x, y, e) -> first((TemplateListSequence) x, e));
        register.add(TemplateListSequence.class, "last", (x, y, e) -> last((TemplateListSequence) x, e));
        register.add(TemplateListSequence.class, REVERSE, (x, y, e) -> reverse((TemplateListSequence) x));
        register.add(TemplateListSequence.class, JOIN, (x, y, e) -> join(y, e, ((TemplateListSequence) x).getSequence()));
        register.add(TemplateListSequence.class, "is_sequence", BuiltInHelper.alwaysTrue());
        register.add(TemplateRightLimitedRange.class, SIZE, (x, y, e) -> TemplateNumber.of(((AbstractLimitedRange) x).size(e)));
        register.add(TemplateRightLimitedRange.class, LOWER, (x, y, e) -> ((AbstractLimitedRange) x).getLower());
        register.add(TemplateRightLimitedRange.class, "upper", (x, y, e) -> ((AbstractLimitedRange) x).getUpper(e));
        register.add(TemplateRightLimitedRange.class, REVERSE, (x, y, e) -> ((TemplateRightLimitedRange) x).reverse());
        register.add(TemplateRightLimitedRange.class, JOIN, (x, y, e) -> join(y, e, ((AbstractLimitedRange) x).getSequence()));
        register.add(TemplateRightLimitedRange.class, IS_RANGE, BuiltInHelper.alwaysTrue());
        register.add(TemplateLengthLimitedRange.class, SIZE, (x, y, e) -> TemplateNumber.of(((TemplateLengthLimitedRange) x).size(e)));
        register.add(TemplateLengthLimitedRange.class, LOWER, (x, y, e) -> ((TemplateLengthLimitedRange) x).getLower());
        register.add(TemplateLengthLimitedRange.class, "upper", (x, y, e) -> ((TemplateLengthLimitedRange) x).getUpper(e));
        register.add(TemplateLengthLimitedRange.class, REVERSE, (x, y, e) -> ((TemplateLengthLimitedRange) x).reverse());
        register.add(TemplateLengthLimitedRange.class, JOIN, (x, y, e) -> join(y, e, ((TemplateLengthLimitedRange) x).getSequence()));
        register.add(TemplateLengthLimitedRange.class, IS_RANGE, BuiltInHelper.alwaysTrue());
        register.add(TemplateRightUnlimitedRange.class, LOWER, (x, y, e) -> ((TemplateRightUnlimitedRange) x).getLower());
        register.add(TemplateRightUnlimitedRange.class, IS_RANGE, BuiltInHelper.alwaysTrue());
        return register;
    }
}
