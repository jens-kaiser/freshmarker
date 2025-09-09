package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateMarkup implements TemplateObject {

    private final TemplateObject content;

    public TemplateMarkup(TemplateObject content) {
        this.content = content;
    }

    @Override
    public TemplateString evaluateToObject(ProcessContext context) {
        TemplateObject templateObject = content.evaluateToObject(context);
        if (templateObject.isNull()) {
            throw new ProcessException("null");
        }
        if (!templateObject.isPrimitive()) {
            throw new ProcessException("missing reduction detected. Unsupported primitive? " + templateObject.getModelType());
        }
        return getString(context, templateObject);
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, content);
    }

    @Override
    public TemplateMarkup reduce(ReduceContext context) {
        TemplateObject templateObject = content.reduce(context);
        if (!templateObject.isPrimitive()) {
            return new TemplateMarkup(templateObject);
        }
        return new TemplateMarkup(getString(context, templateObject));
    }

    private static TemplateString getString(ProcessContext context, TemplateObject templateObject) {
        return switch (templateObject) {
            case TemplateStringMarkup markup -> markup.evaluate(context, TemplateString.class);
            case TemplateString string -> context.getOutputFormat().escape(string);
            default -> {
                String result = context.getFormatter(templateObject.getClass()).format(templateObject, context.getLocale());
                yield context.getOutputFormat().escape(new TemplateString(result));
            }
        };
    }
}
