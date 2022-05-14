package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.UnsupportedBuiltInException;

public class TemplateFunction implements TemplateExpression {

  private final String name;
  private final TemplateObject expression;
  private final List<TemplateObject> parameter;

  public TemplateFunction(String name, TemplateObject expression, List<TemplateObject> parameter) {
    this.name = name;
    this.expression = expression;
    this.parameter = parameter;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateObject result = expression.evaluateToObject(context);
    try {
      return context.getBuiltIn(result.getClass(), name).apply(result, parameter, context);
    } catch (UnsupportedBuiltInException e) {
      if (!(result instanceof TemplateLooper)) {
        throw e;
      }
      result = result.evaluateToObject(context);
      return context.getBuiltIn(result.getClass(), name).apply(result, parameter, context);
    }
  }
}
