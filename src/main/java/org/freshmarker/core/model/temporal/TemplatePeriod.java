package org.freshmarker.core.model.temporal;

import java.time.Period;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplatePeriod extends TemplatePrimitive<Period> {

  public TemplatePeriod(Period value) {
    super(value);
  }

  @Override
  public TemplateObject operation(Token.TokenType operator, TemplateObject operand, ProcessContext context) {
    TemplatePeriod period = operand.evaluate(context, TemplatePeriod.class);
    return switch (operator) {
      case PLUS -> new TemplatePeriod(getValue().plus(period.getValue()));
      case MINUS -> new TemplatePeriod(getValue().minus(period.getValue()));
      default ->  super.operation(operator, operand, context);
    };
  }

  @Override
  public boolean relation(Token.TokenType operator, TemplateObject operand, ProcessContext context) {
    TemplatePeriod rightValue = operand.evaluate(context, TemplatePeriod.class);
    Period period = getValue().minus(rightValue.getValue());
    return switch (operator) {
      case LT -> period.isNegative();
      case GT -> !period.isNegative();
      case LTE -> period.isNegative() || period.isZero();
      case GTE, UNICODE_GTE -> !period.isNegative() || period.isZero();
      default -> super.relation(operator, operand, context);
    };
  }
}
