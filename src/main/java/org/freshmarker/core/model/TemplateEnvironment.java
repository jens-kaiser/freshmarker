package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateEnvironment implements TemplateObject, DotHashAddressable {
    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        return this;
    }

    @Override
    public TemplateObject get(ProcessContext context, String name) {
        String value = System.getProperty(name);
        if (value == null) {
            value = System.getenv(name);
        }
        return new TemplateString(value);
    }
}
