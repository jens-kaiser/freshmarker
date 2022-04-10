package org.freshmarker.core.model;

import ftl.FTLConstants.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateEquality implements TemplateObject {
  private final TokenType type;
  private final TemplateObject left;
  private final TemplateObject right;

  public TemplateEquality(TokenType type, TemplateObject left, TemplateObject right) {
    this.type = type;
    this.left = left;
    this.right = right;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplatePrimitive<?> leftValue = left.evaluate(context, TemplatePrimitive.class);
    TemplatePrimitive<?> rightValue = right.evaluate(context, TemplatePrimitive.class);
    switch (type) {
      case EQUALS:
      case DOUBLE_EQUALS:
        return TemplateBoolean.from(leftValue.getValue().equals(rightValue.getValue()));
      case NOT_EQUALS:
        return TemplateBoolean.from(!leftValue.getValue().equals(rightValue.getValue()));
      default:
        throw new IllegalArgumentException("unsupported releation: " + type);
    }
  }
}
