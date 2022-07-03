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
    switch (type) {
      case LT:
      case ALT_LT:
        return TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() < 0);
      case GT:
      case ALT_GT:
        return TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() > 0);
      case LTE:
      case ALT_LTE:
        return TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() <= 0);
      case GTE:
      case ALT_GTE:
        return TemplateBoolean.from(leftValue.compare(rightValue).sign().asInt() >= 0);
      default:
        throw new IllegalArgumentException("unsupported relation: " + type);
    }
  }

  @Override
  public TemplateObject not() {
    switch (type) {
      case LT:
      case ALT_LT:
        return new TemplateRelational(TokenType.GTE, left, right);
      case GT:
      case ALT_GT:
        return new TemplateRelational(TokenType.LTE, left, right);
      case LTE:
      case ALT_LTE:
        return new TemplateRelational(TokenType.GT, left, right);
      case GTE:
      case ALT_GTE:
        return new TemplateRelational(TokenType.LT, left, right);
      default:
        throw new IllegalArgumentException("unsupported relation: " + type);
    }
  }

  public TokenType getType() {
    return type;
  }

  public TemplateObject getLeft() {
    return left;
  }

  public TemplateObject getRight() {
    return right;
  }
}
