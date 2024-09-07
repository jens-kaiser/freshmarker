package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.AbstractList;
import java.util.List;

public class TemplateRightLimitedRange implements TemplateRange {

    private final TemplateObject lower;
    private final TemplateObject upper;

    private int lowerNumber;
    private int upperNumber;

    public TemplateRightLimitedRange(TemplateObject lower, TemplateObject upper) {
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        return this;
    }

    @Override
    public boolean isLengthLimited() {
        return false;
    }

    @Override
    public boolean isRightUnlimited() {
        return false;
    }

    @Override
    public TemplateObject getLower() {
        return lower;
    }

    @Override
    public TemplateObject getUpper() {
        return upper;
    }

    @Override
    public TemplateNumber get(ProcessContext context, int index) {
        evaluate(context);
        return TemplateNumber.of(lowerNumber < upperNumber ? lowerNumber + index : lowerNumber - index);
    }

    @Override
    public int size(ProcessContext context) {
        evaluate(context);
        return Math.abs(upperNumber - lowerNumber);
    }

    @Override
    public List<Object> getSequence(ProcessContext context) {
        evaluate(context);
        int size = Math.abs(upperNumber - lowerNumber) + 1;
        return new AbstractList<>() {
            @Override
            public Object get(int index) {
                return lowerNumber < upperNumber ? lowerNumber + index : lowerNumber - index;
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    private void evaluate(ProcessContext context) {
        lowerNumber = lowerNumber != 0 ? lowerNumber : lower.evaluate(context, TemplateNumber.class).asInt();
        upperNumber = upperNumber != 0 ? upperNumber : upper.evaluate(context, TemplateNumber.class).asInt();
    }
}
