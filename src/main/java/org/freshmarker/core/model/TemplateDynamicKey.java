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
        TemplateNumber index = dynamicKey.evaluate(context, TemplateNumber.class);
        int beginIndex = index.asInt();
        if (templateObject instanceof TemplateString templateString) {
            String value = templateString.getValue();
            return new TemplateString(value.substring(beginIndex, beginIndex + 1));
        }
        if (templateObject instanceof TemplateRange range) {
            TemplateNumber lower = range.getLower().evaluate(context, TemplateNumber.class);
            if (range.isRightUnlimited()) {
                return lower.add(index);
            }
            TemplateNumber upper = range.getUpper().evaluate(context, TemplateNumber.class);
            if (Math.abs(lower.asInt() - upper.asInt()) <= index.asInt()) {
                throw new ProcessException("index out of range: " + index);
            }
            return lower.add(lower.asInt() < upper.asInt() ? index : index.negate());
        }
        TemplateListSequence list = (TemplateListSequence) templateObject;
        return list.get(context, beginIndex);
    }
}
