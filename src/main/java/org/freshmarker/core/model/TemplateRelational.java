package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;

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
    return TemplateBoolean.from(left.relation(type, right, context));
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
