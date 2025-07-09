package org.freshmarker.core.model.temporal;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.ZonedDateTime;

public class TemplateZonedDateTime extends TemplatePrimitive<ZonedDateTime> implements TemplateDateTime {
    public TemplateZonedDateTime(ZonedDateTime value) {
        super(value);
    }

    @Override
    public TemplatePrimitive<?> relational(Token.TokenType operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateZonedDateTime rightValue = (TemplateZonedDateTime)operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }
}
