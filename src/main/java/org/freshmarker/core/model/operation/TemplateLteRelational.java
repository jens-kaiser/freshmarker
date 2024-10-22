package org.freshmarker.core.model.operation;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.fragment.RelationType;
import org.freshmarker.core.model.TemplateBooleanExpression;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class TemplateLteRelational extends AbstractBinaryExpression implements TemplateBooleanExpression {

    public TemplateLteRelational(TemplateObject left, TemplateObject right) {
        super(left, right);
    }

    @Override
    public TemplateObject evaluate(TemplateObject leftValue, TemplateObject rightValue, ProcessContext processContext) {
        return TemplateBoolean.from(leftValue.relation(RelationType.LTE, rightValue, processContext));
    }

    @Override
    public TemplateGtRelational not() {
        return new TemplateGtRelational(left, right);
    }
}
