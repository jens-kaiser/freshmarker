package org.freshmarker.core.model.temporal;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.OffsetDateTime;

public class TemplateOffsetDateTime extends TemplatePrimitive<OffsetDateTime> implements TemplateDateTime {
    public TemplateOffsetDateTime(OffsetDateTime value) {
        super(value);
    }

    @Override
    public boolean relation(Token.TokenType operator, TemplateObject operand, ProcessContext context) {
        TemplateOffsetDateTime rightValue = operand.evaluate(context, TemplateOffsetDateTime.class);
        return switch (operator) {
            case LT -> getValue().isBefore(rightValue.getValue());
            case GT -> getValue().isAfter(rightValue.getValue());
            case LTE -> getValue().isBefore(rightValue.getValue()) || getValue().isEqual(rightValue.getValue());
            case GTE, UNICODE_GTE -> getValue().isAfter(rightValue.getValue()) || getValue().isEqual(rightValue.getValue());
            default -> super.relation(operator, operand, context);
        };
    }
}
