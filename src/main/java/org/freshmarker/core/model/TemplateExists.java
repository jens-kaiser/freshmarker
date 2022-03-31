package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class TemplateExists implements TemplateObject {

  private final TemplateObject expression;

  public TemplateExists(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    TemplateObject templateObject = expression.evaluateToObject(environment);
    return templateObject == TemplateNull.NULL ? TemplateBoolean.FALSE : TemplateBoolean.TRUE;
  }
}
