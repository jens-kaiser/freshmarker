package org.freshmarker.core.model.temporal;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateRelational.Relation;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.OffsetDateTime;

public class TemplateOffsetDateTime extends TemplatePrimitive<OffsetDateTime> implements TemplateDateTime {
    public TemplateOffsetDateTime(OffsetDateTime value) {
        super(value);
    }

    @Override
    public TemplatePrimitive<?> relational(Relation operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateOffsetDateTime rightValue = (TemplateOffsetDateTime) operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }
}
