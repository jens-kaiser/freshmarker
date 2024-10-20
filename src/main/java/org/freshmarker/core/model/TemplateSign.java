package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public class TemplateSign implements TemplateExpression {

  private final TemplateObject expression;

  public TemplateSign(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return expression.evaluateToObject(context).negate();
  }
}
