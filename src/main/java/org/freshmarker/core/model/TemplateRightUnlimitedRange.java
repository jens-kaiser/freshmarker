package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.AbstractList;
import java.util.List;

public class TemplateRightUnlimitedRange implements TemplateRange {

    private static class UnlimitedList extends AbstractList<Object> {
        private final int leftBound;

        private UnlimitedList(int leftBound) {
            this.leftBound = leftBound;
        }

        @Override
        public Integer get(int index) {
            return leftBound + index;
        }

        @Override
        public int size() {
            return Integer.MAX_VALUE;
        }
    }

    private final TemplateObject lower;
    private Integer lowerNumber;

    public TemplateRightUnlimitedRange(TemplateObject lower) {
        this.lower = lower;
    }

    public TemplateRightUnlimitedRange(TemplateObject lower, int lowerNumber) {
        this.lower = lower;
        this.lowerNumber = lowerNumber;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        if (lowerNumber == null) {
            return new TemplateRightUnlimitedRange(lower, lower.evaluate(context, TemplateNumber.class).asInt());
        }
        return this;
    }

    @Override
    public boolean isRightUnlimited() {
        return true;
    }

    @Override
    public TemplateObject getLower() {
        return lower;
    }

    @Override
    public TemplateNull getUpper(ProcessContext context) {
        return TemplateNull.NULL;
    }

    @Override
    public int size(ProcessContext context) {
        throw new ProcessException("right unlimited range not supported");
    }

    @Override
    public List<Object> sequence() {
        return new UnlimitedList(lowerNumber);
    }

    @Override
    public TemplateRange slice(int min) {
        return new TemplateRightUnlimitedRange(TemplateNumber.of(lowerNumber + min), lowerNumber + min);
    }

    @Override
    public TemplateRange slice(int min, int max) {
        return new TemplateRightLimitedRange(new Bounds(lowerNumber + min, lowerNumber + max));
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, lower);
    }
}
