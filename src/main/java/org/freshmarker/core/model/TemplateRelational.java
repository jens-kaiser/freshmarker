package org.freshmarker.core.model;

import ftl.FTLConstants.TokenType;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateRelational implements TemplateObject {
  private final TokenType type;
  private final TemplateObject left;
  private final TemplateObject right;

  public TemplateRelational(TokenType type, TemplateObject left, TemplateObject right) {
    this.type = type;
    this.left = left;
    this.right = right;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    TemplateNumber leftValue = left.evaluate(environment, TemplateNumber.class);
    TemplateNumber rightValue = right.evaluate(environment, TemplateNumber.class);
    switch (type) {
      case EQUALS:
        return leftValue.compare(rightValue).sign() == 0 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
      case LT:
      case ALT_LT:
        return leftValue.compare(rightValue).sign() < 0 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
      case GT:
      case ALT_GT:
        return leftValue.compare(rightValue).sign() > 0 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
      case NOT_EQUALS:
        return leftValue.compare(rightValue).sign() != 0 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
      case LTE:
      case ALT_LTE:
        return leftValue.compare(rightValue).sign() <= 0 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
      case GTE:
      case ALT_GTE:
        return leftValue.compare(rightValue).sign() >= 0 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
      default:
        throw new IllegalArgumentException("unsupported releation: " + type);
    }
  }
}
