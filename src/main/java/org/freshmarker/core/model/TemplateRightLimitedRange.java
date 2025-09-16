package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
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
        return evaluateBounds(lower.evaluate(context, TemplateNumber.class), upper.evaluate(context, TemplateNumber.class));
    }

    private Bounds evaluateBounds(TemplateNumber lowerNumber, TemplateNumber upperNumber) {
        int newLower = lowerNumber.asInt();
        int newUpper = upperNumber.asInt();
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

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, lower, upper, exclusive);
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        TemplateObject reducedLower = lower.reduce(context);
        TemplateObject reducedUpper = upper.reduce(context);
        if (reducedLower instanceof TemplateNumber lowerNumber && reducedUpper instanceof TemplateNumber upperNumber) {
            if (reducedLower == lower && reducedUpper == upper) {
                bounds = evaluateBounds(lowerNumber, upperNumber);
                return this;
            }
            return new TemplateRightLimitedRange(evaluateBounds(lowerNumber, upperNumber));
        }
        if (reducedLower == lower && reducedUpper == upper) {
            return this;
        }
        return new TemplateRightLimitedRange(reducedLower, reducedUpper, exclusive);
    }
}
