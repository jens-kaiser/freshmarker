package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.api.OutputFormat;

public class TemplateStringMarkup implements TemplateObject {
    private final TemplateString content;

    public TemplateStringMarkup(TemplateString content, OutputFormat outputFormat) {
        this.content = outputFormat.escape(content);
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public TemplateString evaluateToObject(ProcessContext context) {
        return content;
    }
}
