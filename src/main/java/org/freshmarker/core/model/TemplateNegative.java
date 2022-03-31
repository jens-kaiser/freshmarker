package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class TemplateNegative implements TemplateExpression {

  private final TemplateObject expression;

  public TemplateNegative(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    TemplateBoolean templateObject = expression.evaluate(environment, TemplateBoolean.class);
    return templateObject == TemplateBoolean.TRUE ? TemplateBoolean.FALSE : TemplateBoolean.TRUE;
  }
}
