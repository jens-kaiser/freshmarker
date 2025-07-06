package org.freshmarker.core.model.temporal;

import java.time.LocalDateTime;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateLocalDateTime extends TemplatePrimitive<LocalDateTime> implements TemplateDateTime {
  public TemplateLocalDateTime(LocalDateTime value) {
    super(value);
  }

  @Override
  public boolean relation(Token.TokenType operator, TemplateObject operand, ProcessContext context) {
    TemplateLocalDateTime rightValue = operand.evaluate(context, TemplateLocalDateTime.class);
    return switch (operator) {
      case LT -> getValue().isBefore(rightValue.getValue());
      case GT -> getValue().isAfter(rightValue.getValue());
      case LTE -> getValue().isBefore(rightValue.getValue()) || getValue().equals(rightValue.getValue());
      case GTE, UNICODE_GTE -> getValue().isAfter(rightValue.getValue()) || getValue().equals(rightValue.getValue());
      default -> super.relation(operator, operand, context);
    };
  }
}
