package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateMarkup implements TemplateObject {

    private final TemplateObject content;

    public TemplateMarkup(TemplateObject content) {
        this.content = content;
    }

    @Override
    public boolean isMarkup() {
        return true;
    }

    @Override
    public TemplateString evaluateToObject(ProcessContext context) {
        TemplateObject templateObject = content.evaluateToObject(context);
        if (templateObject.isNull()) {
            throw new ProcessException("null");
        }
        if (!templateObject.isPrimitive() && !templateObject.isMarkup()) {
            throw new ProcessException("missing reduction detected. Unsupported primitive? " + templateObject.getModelType());
        }
        if (templateObject.isMarkup()) {
            return templateObject.evaluate(context, TemplateString.class);
        }
        Environment environment = context.getEnvironment();
        if (templateObject instanceof TemplateString) {
            return context.getOutputFormat().escape(environment, templateObject.toString());
        }
        String result = context.getFormatter(templateObject.getClass()).format(templateObject, context.getLocale());
        return context.getOutputFormat().escape(environment, result);
    }
}
