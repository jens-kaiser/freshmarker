package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public record TemplateExists(TemplateObject expression) implements TemplateBooleanExpression {

  @Override
  public TemplateBoolean evaluateToObject(ProcessContext context) {
    TemplateObject templateObject = expression.evaluateToObject(context);
    return context.reductionCheck(templateObject) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
  }

  @Override
  public TemplateNot not() {
    return new TemplateNot(this);
  }

  @Override
  public <R> R accept(TemplateObjectVisitor<R> visitor) {
    return visitor.visit(this);
  }

  @Override
  public TemplateObject reduce(ReduceContext context) {
      TemplateObject result = expression.reduce(context);
      if (result == TemplateNull.NULL) {
          return this;
      }
      return result.isPrimitive() ? TemplateBoolean.TRUE : new TemplateExists(result);
    }
}
