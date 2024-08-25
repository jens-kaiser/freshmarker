package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.OutputFormat;

public class TemplateStringMarkup implements TemplateObject {
    private final TemplateString content;
    private final OutputFormat outputFormat;

    public TemplateStringMarkup(TemplateString content, OutputFormat outputFormat) {
        this.content = content;
        this.outputFormat = outputFormat;
    }

    @Override
    public boolean isMarkup() {
        return true;
    }

    @Override
    public TemplateString evaluateToObject(ProcessContext context) {
        return outputFormat.escape(context.getEnvironment(), content.toString());
    }

    public Class<?> getType() {
        return getClass();
    }

}
