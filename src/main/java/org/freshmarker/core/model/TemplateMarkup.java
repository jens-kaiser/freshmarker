package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
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
        return switch (templateObject) {
            case TemplateStringMarkup markup -> markup.evaluate(context, TemplateString.class);
            case TemplateString string -> context.getOutputFormat().escape(string);
            default -> {
                String result = context.getFormatter(templateObject.getClass()).format(templateObject, context.getLocale());
                yield context.getOutputFormat().escape(new TemplateString(result));
            }
        };
    }

    @Override
    public void accept(TemplateObjectVisitor visitor) {
        visitor.visit(this, content);
    }
}
