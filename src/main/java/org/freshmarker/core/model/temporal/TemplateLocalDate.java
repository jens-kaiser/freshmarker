package org.freshmarker.core.model.temporal;

import ftl.Token;
import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;

public class TemplateLocalDate extends TemplatePrimitive<LocalDate> implements TemplateDate {

    public TemplateLocalDate(LocalDate value) {
        super(value);
    }

    @Override
    public TemplateObject operation(TokenType operator, TemplateObject operand, ProcessContext context) {
        TemplateObject duration = operand.evaluateToObject(context);
        return switch (operator) {
            case PLUS -> new TemplateLocalDate(getValue().plus(getValue(duration)));
            case MINUS -> new TemplateLocalDate(getValue().minus(getValue(duration)));
            default -> super.operation(operator, operand, context);
        };
    }

    private static TemporalAmount getValue(TemplateObject object) {
        if (object instanceof TemplatePeriod period) {
            return period.getValue();
        }
        if (object instanceof TemplateNumber number && !number.getType().isFloatingPoint()) {
            return Period.of(0, 0, number.getValue().intValue());
        }
        throw new ProcessException("wrong type: " + object.getModelType());
    }

    @Override
    public boolean relation(Token.TokenType operator, TemplateObject operand, ProcessContext context) {
        TemplateLocalDate rightValue = operand.evaluate(context, TemplateLocalDate.class);
        return switch (operator) {
            case LT -> getValue().isBefore(rightValue.getValue());
            case GT -> getValue().isAfter(rightValue.getValue());
            case LTE -> getValue().isBefore(rightValue.getValue()) || getValue().equals(rightValue.getValue());
            case GTE, UNICODE_GTE -> getValue().isAfter(rightValue.getValue()) || getValue().equals(rightValue.getValue());
            default -> super.relation(operator, operand, context);
        };
    }
}
