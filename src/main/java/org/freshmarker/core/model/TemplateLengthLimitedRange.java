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

    protected void evaluate(ProcessContext context) {
        if (bounds == null) {
            int newLower = lower.evaluate(context, TemplateNumber.class).asInt();
            int newCount = count.evaluate(context, TemplateNumber.class).asInt();
            int newUpper;
            size = Math.abs(newCount);
            if (newCount == 0) {
                newUpper = newLower;
            } else if (newCount > 0) {
                newUpper = newLower + size - 1;
            } else {
                newUpper = newLower - size + 1;
            }
            bounds = new Bounds(newLower, newUpper);
            evaluatedUpper = TemplateNumber.of(newUpper);
        }
    }

    @Override
    public TemplateObject getUpper(ProcessContext context) {
        evaluate(context);
        return evaluatedUpper;
    }
}
