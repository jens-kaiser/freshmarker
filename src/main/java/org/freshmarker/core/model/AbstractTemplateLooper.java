package org.freshmarker.core.model;

import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.List;

public abstract class AbstractTemplateLooper<T> implements TemplateLooper {
    protected final List<T> sequence;
    protected final int size;
    protected int index;

    public AbstractTemplateLooper(List<T> sequence, int size) {
        this.sequence = sequence;
        this.size = size;
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
        return size == index + 1 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
    }

    public TemplateBoolean hasNext() {
        return size == index + 1 ? TemplateBoolean.FALSE : TemplateBoolean.TRUE;
    }

    public void increment() {
        index++;
    }

    public TemplateObject cycle(List<TemplateObject> cycle) {
        return cycle.get(index % cycle.size());
    }
}