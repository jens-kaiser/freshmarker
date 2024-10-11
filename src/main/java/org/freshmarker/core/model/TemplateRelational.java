package org.freshmarker.core.model;

import ftl.Token.TokenType;
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
  public TemplateBoolean evaluateToObject(ProcessContext context) {
    TemplateNumber leftValue = left.evaluate(context, TemplateNumber.class);
    TemplateNumber rightValue = right.evaluate(context, TemplateNumber.class);
      return switch (type) {
          case LT -> TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() < 0);
          case GT -> TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() > 0);
          case LTE -> TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() <= 0);
          case GTE -> TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() >= 0);
          default -> throw new IllegalArgumentException("unsupported relation: " + type);
      };
  }

  @Override
  public TemplateRelational not() {
      return switch (type) {
          case LT -> new TemplateRelational(TokenType.GTE, left, right);
          case GT -> new TemplateRelational(TokenType.LTE, left, right);
          case LTE -> new TemplateRelational(TokenType.GT, left, right);
          case GTE -> new TemplateRelational(TokenType.LT, left, right);
          default -> throw new IllegalArgumentException("unsupported relation: " + type);
      };
  }
}
