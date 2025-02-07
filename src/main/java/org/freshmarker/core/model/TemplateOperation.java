package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;

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
}
