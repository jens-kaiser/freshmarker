package org.freshmarker.core.model;

import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.List;

public interface TemplateLooper extends TemplateObject {
    TemplateNumber getIndex();

    TemplateNumber getCounter();

    TemplateBoolean isFirst();

    TemplateBoolean isLast();

    TemplateBoolean hasNext();

    void increment();

    int size();

    TemplateObject cycle(List<TemplateObject> cycle);
}
