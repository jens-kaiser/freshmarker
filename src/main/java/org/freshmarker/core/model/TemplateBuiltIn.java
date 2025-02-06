package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.UnsupportedBuiltInException;

public record TemplateBuiltIn(String name, TemplateObject expression, List<TemplateObject> parameter) implements TemplateExpression {

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
