package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.AbstractList;
import java.util.List;

public abstract class AbstractLimitedRange implements TemplateRange {

    protected final TemplateObject lower;
    protected final TemplateObject upper;
    protected Bounds bounds;
    protected int size;

    protected AbstractLimitedRange(TemplateObject lower, TemplateObject upper) {
        this.lower = lower;
        this.upper = upper;
    }

    protected abstract void evaluate(ProcessContext context);

    @Override
    public boolean isLengthLimited() {
        return true;
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
    public TemplateObject getUpper(ProcessContext context) {
        return upper;
    }

    @Override
    public boolean isEmpty(ProcessContext context) {
        return isLengthLimited() && size(context) == 0;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        evaluate(context);
        return this;
    }

    @Override
    public int size(ProcessContext context) {
        evaluate(context);
        return size;
    }

    @Override
    public List<Object> getSequence(ProcessContext context) {
        evaluate(context);
        return new AbstractList<>() {

            @Override
            public Object get(int index) {
                return bounds.offset(index);
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public TemplateRange slice(int min, ProcessContext context) {
        evaluate(context);
        if (size == 0) {
            throw new ProcessException("cannot slice empty range");
        }
        return new TemplateRightLimitedRange(bounds.intersect(min));
    }

    @Override
    public TemplateRange slice(int min, int max, ProcessContext context) {
        evaluate(context);
        if (size == 0) {
            throw new ProcessException("cannot slice empty range");
        }
        return new TemplateRightLimitedRange(bounds.intersect(new Bounds(min, max)));
    }

    public TemplateRange reverse(ProcessContext context) {
        evaluate(context);
        return new TemplateRightLimitedRange(TemplateNumber.of(bounds.upper()), TemplateNumber.of(bounds.lower()), false);
    }
}
