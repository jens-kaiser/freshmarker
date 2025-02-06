package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateDynamicKey implements TemplateExpression {

    private final TemplateObject sequence;
    private final TemplateObject dynamicKey;

    public TemplateDynamicKey(TemplateObject sequence, TemplateObject dynamicKey) {
        this.sequence = sequence;
        this.dynamicKey = dynamicKey;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject templateObject = sequence.evaluateToObject(context);
        if (templateObject == TemplateNull.NULL) {
            return TemplateNull.NULL;
        }
        TemplateObject key = dynamicKey.evaluateToObject(context);
        return switch (key) {
            case TemplateNumber number -> handleIndex(context, templateObject, number);
            case TemplateRange range -> handleRange(context, templateObject, range);
            default -> throw new ProcessException("unsupported type: " + key.getModelType());
        };
    }

    private TemplateObject handleRange(ProcessContext context, TemplateObject templateObject, TemplateRange range) {
        return new TemplateSlice(templateObject, range).evaluateToObject(context);
    }

    private TemplateObject handleIndex(ProcessContext context, TemplateObject templateObject, TemplateNumber index) {
        int beginIndex = index.asInt();
        if (templateObject instanceof TemplateString templateString) {
            return new TemplateString(templateString.getValue().substring(beginIndex, beginIndex + 1));
        }
        if (templateObject instanceof TemplateRange range) {
            TemplateNumber lower = range.getLower().evaluate(context, TemplateNumber.class);
            if (range.isRightUnlimited()) {
                return lower.add(index);
            }
            TemplateNumber upper = range.getUpper(context).evaluate(context, TemplateNumber.class);
            if (Math.abs(lower.asInt() - upper.asInt()) <= index.asInt()) {
                throw new ProcessException("index out of range: " + index);
            }
            return lower.add(lower.asInt() < upper.asInt() ? index : index.negate());
        }
        TemplateListSequence list = (TemplateListSequence) templateObject;
        return list.get(context, beginIndex);
    }

    public void accept(TemplateObjectVisitor visitor) {
        visitor.visit(this, sequence, dynamicKey);
    }
}
