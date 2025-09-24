package org.freshmarker.core.model.temporal;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateRelational.Relation;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.YearMonth;

public class TemplateYearMonth extends TemplatePrimitive<YearMonth> {
    public TemplateYearMonth(YearMonth value) {
        super(value);
    }

    @Override
    public TemplatePrimitive<?> relational(Relation operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateYearMonth rightValue = (TemplateYearMonth) operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }
}
