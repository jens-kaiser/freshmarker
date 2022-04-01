package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateSign implements TemplateExpression {

  private final TemplateObject expression;

  public TemplateSign(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    TemplateNumber templateObject = expression.evaluate(environment, TemplateNumber.class);
    System.out.println(templateObject + " " + templateObject.negate());
    return templateObject.negate();
  }
}
