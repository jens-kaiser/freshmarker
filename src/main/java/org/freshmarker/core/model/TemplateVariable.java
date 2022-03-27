package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateObject;

public class TemplateVariable implements TemplateExpression {

  private final String name;

  public TemplateVariable(String name) {
    this.name = name;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    return environment.getValue(name);
  }
}
