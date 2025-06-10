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
        return switch (value) {
            case TemplateSlice templateSlice -> templateSlice.evaluateToObject(context);
            case TemplateString templateString -> handleSequence(context, templateRange, templateString);
            case TemplateListSequence templateListSequence -> handleSequence(context, templateRange, templateListSequence);
            case TemplateRange templateRangeValue -> handleSequence(context, templateRange, templateRangeValue);
            case TemplateNumber templateNumberValue -> handleNumber(context, templateRange, templateNumberValue);
            default -> throw new UnsupportedDataTypeException("slicing not supported on " + value.getClass().getSimpleName());
        };
    }

    private TemplateObject handleNumber(ProcessContext context, TemplateRange templateRange, TemplateNumber templateNumberValue) {
        checkRanges(context, templateRange);
        int numberValue = templateNumberValue.getValue().intValue();
        if (templateRange instanceof TemplateRightUnlimitedRange unlimitedRange) {
            int value = ((TemplateNumber) unlimitedRange.getLower()).getValue().intValue();
            return TemplateNumber.of(Math.max(numberValue, value));
        }
        if (templateRange instanceof AbstractLimitedRange limitedRange) {
            AbstractLimitedRange newRange = (AbstractLimitedRange) limitedRange.evaluateToObject(context);

            int left = ((TemplateNumber) newRange.getLower()).getValue().intValue();
            int right = ((TemplateNumber) newRange.getUpper(context)).getValue().intValue();
            return TemplateNumber.of(Math.clamp(numberValue, left, right));
        }
        throw new ProcessException("unsupported range: " + templateRange.getModelType());
    }

    private int getInt(TemplateObject value, ProcessContext context) {
        return value.evaluate(context, TemplateNumber.class).asInt();
    }

    private TemplateObject handleSequence(ProcessContext context, TemplateRange templateRange, TemplateRange templateRangeValue) {
        checkRanges(context, templateRange);
        int min = getInt(templateRange.getLower(), context);
        if (templateRange.isRightUnlimited()) {
            return templateRangeValue.slice(min);
        }
        int max = getInt(templateRange.getUpper(context), context);
        if (templateRangeValue.isLengthLimited()) {
            return templateRangeValue.slice(min, Math.min(templateRangeValue.size(context), max));
        }
        return templateRangeValue.slice(min, max);
    }

    private TemplateListSequence handleSequence(ProcessContext context, TemplateRange templateRange, TemplateListSequence templateListSequence) {
        checkRanges(context, templateRange);
        int min = getInt(templateRange.getLower(), context);
        if (templateRange.isRightUnlimited()) {
            return templateListSequence.slice(min);
        }
        int max = getInt(templateRange.getUpper(context), context);
        if (templateRange.isLengthLimited()) {
            return templateListSequence.slice(min, Math.min(templateListSequence.size(context), max));
        }
        return templateListSequence.slice(min, max);
    }

    private TemplateString handleSequence(ProcessContext context, TemplateRange templateRange, TemplateString templateString) {
        checkRanges(context, templateRange);
        int min = getInt(templateRange.getLower(), context);
        if (templateRange.isRightUnlimited()) {
            return new TemplateString(templateString.getValue().substring(min));
        }
        int max = getInt(templateRange.getUpper(context), context);
        if (templateRange.isLengthLimited()) {
            return templateString.substring(min, Math.min(templateString.getValue().length(), max));
        }
        return templateString.substring(min, max);
    }

    private static void checkRanges(ProcessContext context, TemplateRange templateRange) {
        if (templateRange.isEmpty(context)) {
            throw new ProcessException("cannot slice with empty range");
        }
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, sequence, range);
    }
}
