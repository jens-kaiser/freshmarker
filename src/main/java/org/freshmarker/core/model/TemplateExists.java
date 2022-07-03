package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class TemplateExists implements TemplateBooleanExpression {

  private final TemplateObject expression;

  public TemplateExists(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateObject templateObject = expression.evaluateToObject(context);
    return templateObject == TemplateNull.NULL ? TemplateBoolean.FALSE : TemplateBoolean.TRUE;
  }

  @Override
  public TemplateNegative not() {
    return new TemplateNegative(this);
  }
}
