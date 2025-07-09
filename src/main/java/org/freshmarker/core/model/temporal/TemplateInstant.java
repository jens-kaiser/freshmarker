package org.freshmarker.core.model.temporal;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.Instant;

public class TemplateInstant extends TemplatePrimitive<Instant> implements TemplateDateTime {
    public TemplateInstant(Instant value) {
        super(value);
    }

    @Override
    public TemplatePrimitive<?> relational(Token.TokenType operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateInstant rightValue = (TemplateInstant)operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }
}
