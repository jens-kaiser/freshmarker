package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.ProcessContext;

public class TemplateBuiltIn implements TemplateExpression {

  private final String name;
  private final TemplateObject expression;
  private final List<TemplateObject> parameter;

  public TemplateBuiltIn(String name, TemplateObject expression, List<TemplateObject> parameter) {
    this.name = name;
    this.expression = expression;
    this.parameter = parameter;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateObject result = expression.evaluateToObject(context);
    return context.getBuiltIn(result.getClass(), name).apply(result, parameter, context);
  }
}
