package org.freshmarker.core.model.temporal;

import java.time.Duration;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateDuration extends TemplatePrimitive<Duration> {

  public TemplateDuration(Duration value) {
    super(value);
  }

  @Override
  public boolean relation(Token.TokenType operator, TemplateObject operand, ProcessContext context) {
    TemplateDuration rightValue = operand.evaluate(context, TemplateDuration.class);
    Duration period = getValue().minus(rightValue.getValue());
    return switch (operator) {
      case LT -> period.isNegative();
      case GT -> !period.isNegative();
      case LTE -> period.isNegative() || period.isZero();
      case GTE, UNICODE_GTE -> !period.isNegative() || period.isZero();
      default -> super.relation(operator, operand, context);
    };
  }
}
