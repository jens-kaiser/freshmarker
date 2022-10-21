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
    TemplateObject value = context.getEnvironment().getValue(name);
    if (value instanceof TemplateLoopVariable loopVariable) {
      return loopVariable.evaluateToObject(context);
    }
    return value;
  }
}
