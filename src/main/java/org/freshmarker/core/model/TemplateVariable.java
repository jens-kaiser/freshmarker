package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public record TemplateVariable(String name) implements TemplateExpression {

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateObject value = context.getEnvironment().getValue(name);
    if (value instanceof TemplateLoopVariable loopVariable) {
      return loopVariable.evaluateToObject(context);
    }
    return value;
  }

  @Override
  public void accept(TemplateObjectVisitor visitor) {
    visitor.visit(this, name);
  }
}
