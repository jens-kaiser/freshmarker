package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.Environment;

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
  public TemplateObject evaluateToObject(Environment environment) {
    TemplateObject result = expression.evaluateToObject(environment);
    return environment.getBuildIn(result.getClass(), name).apply(result, parameter, environment);
  }
}
