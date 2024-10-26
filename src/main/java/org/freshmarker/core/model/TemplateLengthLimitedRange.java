package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
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
            if (newCount < 0) {
                throw new ProcessException("count not positive: " + newCount);
            }
            int newUpper;
            if (newCount == 0) {
                size = 0;
                newUpper = newLower;
            } else {
                size = newCount;
                newUpper = newLower + newCount - 1;
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
