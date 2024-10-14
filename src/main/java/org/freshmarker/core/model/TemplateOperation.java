package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;

public class TemplateOperation implements TemplateExpression {

    private final TokenType op;
    private final TemplateObject left;
    private final TemplateObject right;

    public TemplateOperation(TokenType op, TemplateObject left, TemplateObject right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext processContext) {
        TemplateObject leftValue = left.evaluateToObject(processContext);
        TemplateObject rightValue = right.evaluateToObject(processContext);
        return leftValue.operation(op, rightValue, processContext);
    }
}
