package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

public record TemplateSign(TemplateObject expression) implements TemplateExpression {

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
      return expression.evaluateToObject(context).negate();
  }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        return expression.reduce(context).negate();
    }
}
