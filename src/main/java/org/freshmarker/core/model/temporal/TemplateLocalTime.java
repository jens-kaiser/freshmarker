package org.freshmarker.core.model.temporal;

import java.time.LocalTime;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateLocalTime extends TemplatePrimitive<LocalTime> implements TemplateTime {

  public TemplateLocalTime(LocalTime value) {
    super(value);
  }

  @Override
  public boolean relation(Token.TokenType operator, TemplateObject operand, ProcessContext context) {
    TemplateLocalTime rightValue = operand.evaluate(context, TemplateLocalTime.class);
    return switch (operator) {
      case LT -> getValue().isBefore(rightValue.getValue());
      case GT -> getValue().isAfter(rightValue.getValue());
      case LTE -> getValue().isBefore(rightValue.getValue()) || getValue().equals(rightValue.getValue());
      case GTE, UNICODE_GTE -> getValue().isAfter(rightValue.getValue()) || getValue().equals(rightValue.getValue());
      default -> super.relation(operator, operand, context);
    };
  }
}
