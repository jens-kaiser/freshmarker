package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.ProcessContext;

public class TemplateMethodCall implements TemplateExpression {

  private final String name;
  private final List<TemplateObject> parameter;

  public TemplateMethodCall(String name, List<TemplateObject> parameter) {
    this.name = name;
    this.parameter = parameter;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return context.getFunction(name).execute(context, parameter);
  }
}
