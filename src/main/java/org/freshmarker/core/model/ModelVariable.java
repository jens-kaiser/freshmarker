package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

public record ModelVariable(String name) implements TemplateVariable {

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
      return context.getBaseEnvironment().getValue(name);
  }

  @Override
  public <R> R accept(TemplateObjectVisitor<R> visitor) {
    return visitor.visit(this, name);
  }

  @Override
  public TemplateObject reduce(ReduceContext context) {
    TemplateObject value = evaluateToObject(context);
      if (value == TemplateNull.NULL) {
          return this;
      }
      context.getStatus().expression().incrementAndGet();
      return value;
  }
}
