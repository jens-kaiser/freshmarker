package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

import java.util.Map.Entry;

public record TemplateHash(Entry<String, Object> entry) implements TemplateObject {

    @Override
    public TemplateHash evaluateToObject(ProcessContext context) {
        return this;
    }
}