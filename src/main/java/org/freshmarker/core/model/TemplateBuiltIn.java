package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.UnsupportedBuiltInException;

public record TemplateBuiltIn(String name, TemplateObject expression, List<TemplateObject> parameter, boolean ignoreOptionalNull, boolean ignoreNull) implements TemplateExpression {
  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateObject result = expression.evaluateToObject(context);
    if (result == TemplateNull.NULL_OPTIONAL && ignoreOptionalNull) {
      return result;
    }
    if (result == TemplateNull.NULL && ignoreNull) {
      return result;
    }
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

  @Override
  public <R> R accept(TemplateObjectVisitor<R> visitor) {
    return visitor.visit(this);
  }
}
