package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateMarkup implements TemplateObject {

    private final TemplateObject content;

    public TemplateMarkup(TemplateObject content) {
        if (content.isMarkup()) {
            this.content = ((TemplateMarkup) content).content;
        } else {
            this.content = content;
        }
    }

    @Override
    public boolean isMarkup() {
        return true;
    }

    @Override
    public TemplateString evaluateToObject(ProcessContext context) {
        TemplateObject templateObject = getTemplateObject(context);
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

    private TemplateObject getTemplateObject(ProcessContext context) {
        TemplateObject templateObject = content;
        do {
            if (templateObject.isNull()) {
                throw new ProcessException("null");
            }
            TemplateObject last = templateObject;
            templateObject = templateObject.evaluateToObject(context);
            if (last == templateObject && !templateObject.isPrimitive()) {
                throw new ProcessException("missing reduction detected. Unsupported primitive? " + templateObject.getModelType());
            }
        } while (!templateObject.isNull() && !templateObject.isPrimitive() && !templateObject.isMarkup());
        if (templateObject.isNull()) {
            throw new ProcessException("null");
        }
        return templateObject;
    }
}
