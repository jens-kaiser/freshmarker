package org.freshmarker.core.model.temporal;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateOperation.Operator;
import org.freshmarker.core.model.TemplateRelational.Relation;
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
    public TemplateObject operation(Operator operator, TemplateObject operand, ProcessContext context) {
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
    public TemplatePrimitive<?> relational(Relation operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateYear rightValue = (TemplateYear) operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }
}
