package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateLengthLimitedRange extends AbstractLimitedRange {

    private final TemplateObject count;
    private TemplateObject evaluatedUpper;

    public TemplateLengthLimitedRange(TemplateObject lower, TemplateObject count) {
        super(lower, null);
        this.count = count;
    }

    public TemplateLengthLimitedRange(TemplateObject lower, TemplateObject upper, TemplateNumber count, Bounds bounds) {
        super(lower, upper, bounds);
        this.evaluatedUpper = upper;
        this.count = count;
    }

    protected Bounds evaluate(ProcessContext context) {
        int newLower = lower.evaluate(context, TemplateNumber.class).asInt();
        int newCount = count.evaluate(context, TemplateNumber.class).asInt();
        int newUpper;
        int size = Math.abs(newCount);
        if (newCount == 0) {
            newUpper = newLower;
        } else if (newCount > 0) {
            newUpper = newLower + size - 1;
        } else {
            newUpper = newLower - size + 1;
        }
        return new Bounds(newLower, newUpper, size);
    }

    @Override
    public boolean isLengthLimited() {
        return true;
    }

    @Override
    public TemplateObject getUpper(ProcessContext context) {
        evaluate(context);
        return evaluatedUpper;
    }

    @Override
    protected TemplateRange newRange(Bounds bounds) {
        int diff = bounds.upper() - bounds.lower();
        int offset = Integer.compare(bounds.upper(), bounds.lower());
        return new TemplateLengthLimitedRange(TemplateNumber.of(bounds.lower()), TemplateNumber.of(bounds.upper()), TemplateNumber.of(diff + offset), bounds);
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, lower, upper, count);
    }
}
