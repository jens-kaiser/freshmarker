package org.freshmarker.core.model.primitive;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public class TemplateString extends TemplatePrimitive<String> {
    public static final TemplateString EMPTY = new TemplateString("");

    public TemplateString(String value) {
        super(value);
    }

    public TemplateString concat(TemplateString other) {
        if (getValue().isEmpty()) {
            return other;
        }
        if (other.getValue().isEmpty()) {
            return this;
        }
        return new TemplateString(getValue() + other.getValue());
    }

    @Override
    public TemplateObject add(TemplateObject operand, ProcessContext context) {
        return concat(operand.evaluate(context, TemplateString.class));
    }
}
