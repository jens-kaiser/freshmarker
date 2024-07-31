package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateSign implements TemplateExpression {

  private final TemplateObject expression;

  public TemplateSign(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public TemplateNumber evaluateToObject(ProcessContext context) {
    return expression.evaluate(context, TemplateNumber.class).negate();
  }
}
