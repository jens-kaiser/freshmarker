package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public class TemplateVariable implements TemplateExpression {

  private final String name;

  public TemplateVariable(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return context.getEnvironment().getValue(name);
  }
}
