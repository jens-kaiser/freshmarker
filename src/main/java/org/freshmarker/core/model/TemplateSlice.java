package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.UnsupportedDataTypeException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateSlice implements TemplateObject {

    private final TemplateObject sequence;
    private final TemplateObject range;

    public TemplateSlice(TemplateObject sequence, TemplateObject range) {
        this.sequence = sequence;
        this.range = range;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateRange templateRange = range.evaluate(context, TemplateRange.class);
        TemplateObject value = sequence.evaluateToObject(context);
        if (value instanceof TemplateString templateString) {
            return handleSequence(context, templateRange, templateString);
        } else if (value instanceof TemplateListSequence templateListSequence) {
            return handleSequence(context, templateRange, templateListSequence);
        } else if (value instanceof TemplateRange templateRangeValue) {
            return handleSequence(context, templateRange, templateRangeValue);
        }
        throw new UnsupportedDataTypeException("slicing not supported on " + value.getClass().getSimpleName());
    }

    private int getInt(TemplateObject value, ProcessContext context) {
        return value.evaluate(context, TemplateNumber.class).getValue().intValue();
    }

    private TemplateObject handleSequence(ProcessContext context, TemplateRange templateRange, TemplateRange templateRangeValue) {
        int min = getInt(templateRange.getLower(), context);
        if (templateRange.isRightUnlimited()) {
            return templateRangeValue.slice(min, context);
        }
        int max = getInt(templateRange.getUpper(), context);
        checkSlice(min, max);
        if (templateRange.isLengthLimited()) {
            return templateRangeValue.slice(min, Math.min(templateRangeValue.size(context), max), context);
        }
        return templateRangeValue.slice(min, max, context);
    }

    private void checkSlice(int min, int max) {
        if (min > max) {
            throw new ProcessException("inverted slices not supported: " + min + ".." + max);
        }
    }

    private TemplateListSequence handleSequence(ProcessContext context, TemplateRange templateRange, TemplateListSequence templateListSequence) {
        TemplateNumber lower = templateRange.getLower().evaluate(context, TemplateNumber.class);
        int min = lower.getValue().intValue();
        if (templateRange.isRightUnlimited()) {
            return templateListSequence.slice(min);
        }
        TemplateNumber upper = templateRange.getUpper().evaluate(context, TemplateNumber.class);
        int max = upper.getValue().intValue() + 1;
        checkSlice(min, max);
        if (templateRange.isLengthLimited()) {
            return templateListSequence.slice(min, Math.min(templateListSequence.size(context), max));
        }
        return templateListSequence.slice(min, max);
    }

    private TemplateString handleSequence(ProcessContext context, TemplateRange templateRange, TemplateString templateString) {
        String value = templateString.getValue();
        TemplateNumber lower = templateRange.getLower().evaluate(context, TemplateNumber.class);
        int min = lower.getValue().intValue();
        if (templateRange.isRightUnlimited()) {
            return new TemplateString(value.substring(min));
        }
        TemplateNumber upper = templateRange.getUpper().evaluate(context, TemplateNumber.class);
        int max = upper.getValue().intValue() + 1;
        checkSlice(min, max);
        if (templateRange.isLengthLimited()) {
            return new TemplateString(value.substring(min, Math.min(value.length(), max)));
        }
        return new TemplateString(value.substring(min, max));
    }
}
