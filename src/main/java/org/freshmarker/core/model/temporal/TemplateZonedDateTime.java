package org.freshmarker.core.model.temporal;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateRelational.Relation;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.ZonedDateTime;

public class TemplateZonedDateTime extends TemplatePrimitive<ZonedDateTime> implements TemplateDateTime {
    public TemplateZonedDateTime(ZonedDateTime value) {
        super(value);
    }

    @Override
    public TemplatePrimitive<?> relational(Relation operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateZonedDateTime rightValue = (TemplateZonedDateTime) operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }
}
