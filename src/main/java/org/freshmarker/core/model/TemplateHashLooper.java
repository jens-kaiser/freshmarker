package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

import java.util.List;
import java.util.Map.Entry;

public class TemplateHashLooper extends AbstractTemplateLooper<Entry<String, Object>> {

    public TemplateHashLooper(List<Entry<String, Object>> sequence) {
        super(sequence);
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        if (current == null) {
            current = new TemplateHash(sequence.get(index));
        }
        return current;
    }
}