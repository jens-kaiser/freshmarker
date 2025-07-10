package org.freshmarker.core.model.primitive;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateObjectVisitor;

public class TemplateString extends TemplatePrimitive<String> {
    public static final TemplateString EMPTY = new TemplateString("");

    public TemplateString(String value) {
        super(value);
    }

    public TemplateString concat(TemplateString other, String seperator) {
        if (getValue().isEmpty()) {
            return other;
        }
        if (other.getValue().isEmpty()) {
            return this;
        }
        return new TemplateString(getValue() + seperator + other.getValue());
    }

    @Override
    public TemplateObject operation(TokenType operator, TemplateObject operand, ProcessContext context) {
        return switch (operator) {
            case PLUS -> concat(operand.evaluate(context, TemplateString.class), "");
            case CONCAT -> concat(operand.evaluate(context, TemplateString.class), " ");
            default -> super.operation(operator, operand, context);
        };
    }

    private int checkLowerBound(int lowerBound) {
        if (lowerBound < 0) {
            throw new ProcessException("negative slicing values not allowed: " + lowerBound);
        }
        return lowerBound;
    }

    public TemplateString substring(int min, int max) {
        if (min < max) {
            return new TemplateString(getValue().substring(checkLowerBound(min), max + 1));
        }
        return new TemplateString(new StringBuilder(getValue().substring(checkLowerBound(max), min + 1)).reverse().toString());
    }

    @Override
    public TemplatePrimitive<?> relational(TokenType operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateString rightValue = (TemplateString) operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));

    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, "'" + this + "'");
    }

}
