package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateHashLoopVariable implements TemplateLoopVariable {

    private final TemplateHashLooper looper;
    private final boolean key;

    public TemplateHashLoopVariable(TemplateHashLooper looper, boolean key) {
        this.looper = looper;
        this.key = key;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateHash hash = looper.evaluate(context, TemplateHash.class);
        return key ? new TemplateString(hash.entry().getKey()) : context.getBaseEnvironment().mapObject(hash.entry().getValue());
    }
}