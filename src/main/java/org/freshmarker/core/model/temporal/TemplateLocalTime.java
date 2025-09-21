package org.freshmarker.core.model.temporal;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateRelational.Relation;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.LocalTime;

public class TemplateLocalTime extends TemplatePrimitive<LocalTime> implements TemplateTime {

    public TemplateLocalTime(LocalTime value) {
        super(value);
    }

    @Override
    public TemplatePrimitive<?> relational(Relation operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateLocalTime rightValue = (TemplateLocalTime) operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }
}
