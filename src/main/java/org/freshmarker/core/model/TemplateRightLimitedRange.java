package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateRightLimitedRange extends AbstractLimitedRange {

    private final boolean exclusive;

    public TemplateRightLimitedRange(TemplateObject lower, TemplateObject upper, boolean exclusive) {
        super(lower, upper);
        this.exclusive = exclusive;
    }

    TemplateRightLimitedRange(Bounds bounds) {
        super(TemplateNumber.of(bounds.lower()), TemplateNumber.of(bounds.upper()), bounds);
        this.exclusive = false;
    }

    @Override
    protected Bounds evaluate(ProcessContext context) {
        int newLower = lower.evaluate(context, TemplateNumber.class).asInt();
        int newUpper = upper.evaluate(context, TemplateNumber.class).asInt();
        int size = Math.abs(newLower - newUpper) + 1;
        if (exclusive) {
            size--;
            newUpper = newLower < newUpper ? newUpper - 1 : newUpper + 1;
        }
        return new Bounds(newLower, newUpper, size);
    }

    @Override
    protected TemplateRange newRange(Bounds bounds) {
        return new TemplateRightLimitedRange(bounds);
    }
}
