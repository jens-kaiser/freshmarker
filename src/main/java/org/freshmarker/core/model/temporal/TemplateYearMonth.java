package org.freshmarker.core.model.temporal;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.YearMonth;

public class TemplateYearMonth extends TemplatePrimitive<YearMonth> {
    public TemplateYearMonth(YearMonth value) {
        super(value);
    }

    @Override
    public TemplatePrimitive<?> relational(Token.TokenType operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateYearMonth rightValue = (TemplateYearMonth)operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }
}
