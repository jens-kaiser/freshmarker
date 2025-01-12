package org.freshmarker.core.model.temporal;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.Year;

public class TemplateYear extends TemplatePrimitive<Year> {
    public TemplateYear(Year value) {
        super(value);
    }

    public TemplateYear(int value) {
        super(Year.of(value));
    }

    public boolean isLeap() {
        return getValue().isLeap();
    }
}
