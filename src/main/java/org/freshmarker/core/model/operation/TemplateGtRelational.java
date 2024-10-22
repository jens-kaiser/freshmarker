package org.freshmarker.core.model.operation;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.fragment.RelationType;
import org.freshmarker.core.model.TemplateBooleanExpression;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class TemplateGtRelational extends AbstractBinaryExpression implements TemplateBooleanExpression {

    public TemplateGtRelational(TemplateObject left, TemplateObject right) {
        super(left, right);
    }

    @Override
    public TemplateObject evaluate(TemplateObject leftValue, TemplateObject rightValue, ProcessContext processContext) {
        return TemplateBoolean.from(leftValue.relation(RelationType.GT, rightValue, processContext));
    }

    @Override
    public TemplateLteRelational not() {
        return new TemplateLteRelational(left, right);
    }
}

