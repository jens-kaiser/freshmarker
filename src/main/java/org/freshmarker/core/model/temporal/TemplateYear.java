package org.freshmarker.core.model.temporal;

import ftl.Token;
import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.Period;
import java.time.Year;
import java.time.temporal.TemporalAmount;

public class TemplateYear extends TemplatePrimitive<Year> {
    public TemplateYear(Year value) {
        super(value);
    }

    public TemplateYear(int value) {
        super(Year.of(value));
    }

    public boolean isLeap() {
        return getValue().isLeap();
    }

    @Override
    public TemplateObject operation(TokenType operator, TemplateObject operand, ProcessContext context) {
        TemplateObject duration = operand.evaluateToObject(context);
        return switch (operator) {
            case PLUS -> new TemplateYear(getValue().plus(getValue(duration)));
            case MINUS -> new TemplateYear(getValue().minus(getValue(duration)));
            default -> super.operation(operator, operand, context);
        };
    }

    private static TemporalAmount getValue(TemplateObject object) {
        if (object instanceof TemplatePeriod period) {
            return period.getValue();
        }
        if (object instanceof TemplateNumber number && !number.getType().isFloatingPoint()) {
            return Period.of(number.getValue().intValue(), 0, 0);
        }
        throw new ProcessException("wrong type: " + object.getModelType());
    }

    @Override
    public boolean relation(Token.TokenType operator, TemplateObject operand, ProcessContext context) {
        TemplateYear rightValue = operand.evaluate(context, TemplateYear.class);
        return switch (operator) {
            case LT -> getValue().isBefore(rightValue.getValue());
            case GT -> getValue().isAfter(rightValue.getValue());
            case LTE -> getValue().isBefore(rightValue.getValue()) || getValue().equals(rightValue.getValue());
            case GTE, UNICODE_GTE -> getValue().isAfter(rightValue.getValue()) || getValue().equals(rightValue.getValue());
            default -> super.relation(operator, operand, context);
        };
    }
}
