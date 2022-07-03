package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateEquality implements TemplateBooleanExpression {

  private final TemplateObject left;
  private final TemplateObject right;

  public TemplateEquality(TemplateObject left, TemplateObject right) {
    this.left = left;
    this.right = right;
  }

  public TemplateNegative not() {
    return new TemplateNegative(this);
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplatePrimitive<?> leftValue = left.evaluate(context, TemplatePrimitive.class);
    TemplatePrimitive<?> rightValue = right.evaluate(context, TemplatePrimitive.class);
    return TemplateBoolean.from(leftValue.getValue().equals(rightValue.getValue()));
  }

  public TemplateObject getLeft() {
    return left;
  }

  public TemplateObject getRight() {
    return right;
  }
}
