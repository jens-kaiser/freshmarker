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
        super(TemplateNumber.of(bounds.lower()), TemplateNumber.of(bounds.upper()));
        this.exclusive = false;
    }

    @Override
    protected void evaluate(ProcessContext context) {
        if (bounds == null) {
            int newLower = lower.evaluate(context, TemplateNumber.class).asInt();
            int newUpper = upper.evaluate(context, TemplateNumber.class).asInt();
            if (exclusive) {
                size = Math.abs(newLower - newUpper);
                newUpper = newLower < newUpper ? newUpper - 1 : newUpper + 1;
            } else {
                size = Math.abs(newLower - newUpper) + 1;
            }
            bounds = new Bounds(newLower, newUpper);
        }
    }

    @Override
    protected TemplateRange newRange(Bounds bounds) {
        return new TemplateRightLimitedRange(bounds);
    }
}
