package org.freshmarker.core.model.operation;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public abstract class AbstractBinaryExpression implements TemplateObject {
    protected final TemplateObject left;
    protected final TemplateObject right;

    public AbstractBinaryExpression(TemplateObject left, TemplateObject right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public final TemplateObject evaluateToObject(ProcessContext processContext) {
        TemplateObject leftValue = left.evaluateToObject(processContext);
        TemplateObject rightValue = right.evaluateToObject(processContext);
        return evaluate(leftValue, rightValue, processContext);
    }

    public abstract TemplateObject evaluate(TemplateObject leftValue, TemplateObject rightValue, ProcessContext processContext);
}
