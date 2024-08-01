package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class TemplateExists implements TemplateBooleanExpression {

  private final TemplateObject expression;

  public TemplateExists(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public TemplateBoolean evaluateToObject(ProcessContext context) {
    TemplateObject templateObject = expression.evaluateToObject(context);
    return context.reductionCheck(templateObject) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
  }

  @Override
  public TemplateNegative not() {
    return new TemplateNegative(this);
  }
}
