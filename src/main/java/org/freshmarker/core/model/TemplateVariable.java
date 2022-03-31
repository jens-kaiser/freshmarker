package org.freshmarker.core.model;

import org.freshmarker.core.Environment;

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
