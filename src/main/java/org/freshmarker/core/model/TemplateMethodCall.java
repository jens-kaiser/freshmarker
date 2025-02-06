package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.ProcessContext;

public record TemplateMethodCall(String name, List<TemplateObject> parameter) implements TemplateExpression {

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return context.getFunction(name).execute(context, parameter);
  }

  @Override
  public void accept(TemplateObjectVisitor visitor) {
      visitor.visit(this);
  }
}
