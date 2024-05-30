package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateJunction implements TemplateBooleanExpression {
  private final TokenType type;
  private final TemplateObject left;
  private final TemplateObject right;

  public TemplateJunction(TokenType type, TemplateObject left, TemplateObject right) {
    this.type = type;
    this.left = left;
    this.right = right;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateBoolean leftValue = left.evaluate(context, TemplateBoolean.class);
      return switch (type) {
          case AND -> TemplateBoolean.from(leftValue.getValue() & right.evaluate(context, TemplateBoolean.class).getValue());
          case AND2 -> TemplateBoolean.from(leftValue.getValue() && right.evaluate(context, TemplateBoolean.class).getValue());
          case OR -> TemplateBoolean.from(leftValue.getValue() | right.evaluate(context, TemplateBoolean.class).getValue());
          case OR2 -> TemplateBoolean.from(leftValue.getValue() || right.evaluate(context, TemplateBoolean.class).getValue());
          case XOR -> TemplateBoolean.from(leftValue.getValue() ^ right.evaluate(context, TemplateBoolean.class).getValue());
          default -> throw new IllegalArgumentException("unsupported relation: " + type);
      };
  }

  @Override
  public TemplateObject not() {
      return new TemplateNegative(this);
  }
}
