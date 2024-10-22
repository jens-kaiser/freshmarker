package org.freshmarker.core.model.operation;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public class TemplateMulOperation extends AbstractBinaryExpression {

    public TemplateMulOperation(TemplateObject left, TemplateObject right) {
        super(left, right);
    }

    @Override
    public TemplateObject evaluate(TemplateObject leftValue, TemplateObject rightValue, ProcessContext processContext) {
        return leftValue.multiply(rightValue, processContext);
    }
}
