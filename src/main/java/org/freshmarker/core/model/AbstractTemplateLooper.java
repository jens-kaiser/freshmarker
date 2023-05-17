package org.freshmarker.core.model;

import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.List;

public abstract class AbstractTemplateLooper<T> implements TemplateLooper {
    protected final List<T> sequence;
    protected int index;

    public AbstractTemplateLooper(List<T> sequence) {
        this.sequence = sequence;
    }

    public TemplateNumber getIndex() {
        return new TemplateNumber(index);
    }

    public TemplateNumber getCounter() {
        return new TemplateNumber(index + 1);
    }

    public TemplateBoolean isFirst() {
        return index == 0 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
    }

    public TemplateBoolean isLast() {
        return sequence.size() == index + 1 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
    }

    public TemplateBoolean hasNext() {
        return sequence.size() == index + 1 ? TemplateBoolean.FALSE : TemplateBoolean.TRUE;
    }

    public void increment() {
        index++;
    }

    public int size() {
        return sequence.size();
    }

    public TemplateObject cycle(List<TemplateObject> cycle) {
        return cycle.get(index % cycle.size());
    }
}