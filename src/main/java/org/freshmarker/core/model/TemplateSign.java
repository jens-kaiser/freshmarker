package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public record TemplateSign(TemplateObject expression) implements TemplateExpression {

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return expression.evaluateToObject(context).negate();
  }
}
