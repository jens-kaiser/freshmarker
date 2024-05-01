package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public record TemplateEquality(TemplateObject left, TemplateObject right) implements TemplateBooleanExpression {

  public TemplateNegative not() {
    return new TemplateNegative(this);
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplatePrimitive<?> leftValue = left.evaluate(context, TemplatePrimitive.class);
    TemplatePrimitive<?> rightValue = right.evaluate(context, TemplatePrimitive.class);
    return TemplateBoolean.from(leftValue.getValue().equals(rightValue.getValue()));
  }
}
