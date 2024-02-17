package org.freshmarker.core.model;

import ftl.FTLConstants.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateRelational implements TemplateBooleanExpression {
  private final TokenType type;
  private final TemplateObject left;
  private final TemplateObject right;

  public TemplateRelational(TokenType type, TemplateObject left, TemplateObject right) {
    this.type = type;
    this.left = left;
    this.right = right;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateNumber leftValue = left.evaluate(context, TemplateNumber.class);
    TemplateNumber rightValue = right.evaluate(context, TemplateNumber.class);
      return switch (type) {
          case LT, ALT_LT -> TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() < 0);
          case GT, ALT_GT -> TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() > 0);
          case LTE, ALT_LTE -> TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() <= 0);
          case GTE, ALT_GTE -> TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() >= 0);
          default -> throw new IllegalArgumentException("unsupported relation: " + type);
      };
  }

  @Override
  public TemplateObject not() {
      return switch (type) {
          case LT, ALT_LT -> new TemplateRelational(TokenType.GTE, left, right);
          case GT, ALT_GT -> new TemplateRelational(TokenType.LTE, left, right);
          case LTE, ALT_LTE -> new TemplateRelational(TokenType.GT, left, right);
          case GTE, ALT_GTE -> new TemplateRelational(TokenType.LT, left, right);
          default -> throw new IllegalArgumentException("unsupported relation: " + type);
      };
  }
}
