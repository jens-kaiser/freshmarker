package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public record DefaultTemplateVariable(String name) implements TemplateVariable {

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
      return context.getEnvironment().getValue(name);
  }

  @Override
  public <R> R accept(TemplateObjectVisitor<R> visitor) {
    return visitor.visit(this, name);
  }
}
