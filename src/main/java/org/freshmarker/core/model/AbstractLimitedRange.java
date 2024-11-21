package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;

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

    protected AbstractLimitedRange(TemplateObject lower, TemplateObject upper, Bounds bounds) {
        this.lower = lower;
        this.upper = upper;
        this.bounds = bounds;
        this.size = bounds.size();
    }

    protected abstract Bounds evaluate(ProcessContext context);

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
        return size(context) == 0;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        if (bounds == null) {
            return newRange(evaluate(context));
        }
        return this;
    }

    @Override
    public int size(ProcessContext context) {
        return bounds.size();
    }

    @Override
    public List<Object> getSequence(ProcessContext context) {
        return new AbstractList<>() {

            @Override
            public Object get(int index) {
                return bounds.offset(index);
            }

            @Override
            public int size() {
                return bounds.size();
            }
        };
    }

    protected abstract TemplateRange newRange(Bounds bounds);

    @Override
    public TemplateRange slice(int min, ProcessContext context) {
        if (bounds.size() == 0) {
            throw new ProcessException("cannot slice empty range");
        }
        return newRange(bounds.intersect(min));
    }

    @Override
    public TemplateRange slice(int min, int max, ProcessContext context) {
        if (bounds.size() == 0) {
            throw new ProcessException("cannot slice empty range");
        }
        return newRange(bounds.intersect(new Bounds(min, max, Math.abs(max - min) + 1)));
    }

    public TemplateRange reverse(ProcessContext context) {
        return newRange(new Bounds(bounds.upper(), bounds.lower(), bounds.size()));
    }
}
