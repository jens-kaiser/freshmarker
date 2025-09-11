package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;

public record TemplateOperation(TokenType op, TemplateObject left, TemplateObject right) implements TemplateExpression {

    @Override
    public TemplateObject evaluateToObject(ProcessContext processContext) {
        TemplateObject leftValue = left.evaluateToObject(processContext);
        TemplateObject rightValue = right.evaluateToObject(processContext);
        return leftValue.operation(op, rightValue, processContext);
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public TemplateObject reduce(ReduceContext processContext) {
        TemplateObject leftValue = left.reduce(processContext);
        TemplateObject rightValue = right.reduce(processContext);
        try {
            TemplateObject result = leftValue.operation(op, rightValue, processContext);
            processContext.getStatus().expression().addAndGet(2);
            return result;
        } catch (ProcessException e) {
            return new TemplateOperation(op, leftValue, rightValue);
        }
    }
}
