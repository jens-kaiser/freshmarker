package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class TemplateNegative implements TemplateBooleanExpression {

  private final TemplateObject expression;

  public TemplateNegative(TemplateObject expression) {
    this.expression = expression;
  }

  public TemplateObject not() {
    return expression;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateBoolean templateObject = expression.evaluate(context, TemplateBoolean.class);
    return templateObject == TemplateBoolean.TRUE ? TemplateBoolean.FALSE : TemplateBoolean.TRUE;
  }
}
