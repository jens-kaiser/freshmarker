package org.freshmarker.core.model.temporal;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.Period;

public class TemplatePeriod extends TemplatePrimitive<Period> {

    public TemplatePeriod(Period value) {
        super(value);
    }

    @Override
    public TemplateObject add(TemplateObject operand, ProcessContext context) {
        TemplatePeriod period = operand.evaluate(context, TemplatePeriod.class);
        return new TemplatePeriod(getValue().plus(period.getValue()));
    }

    @Override
    public TemplateObject subtract(TemplateObject operand, ProcessContext context) {
        TemplatePeriod period = operand.evaluate(context, TemplatePeriod.class);
        return new TemplatePeriod(getValue().minus(period.getValue()));
    }
}
