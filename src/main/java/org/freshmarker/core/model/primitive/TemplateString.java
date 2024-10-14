package org.freshmarker.core.model.primitive;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public class TemplateString extends TemplatePrimitive<String> {
  public static final TemplateString EMPTY = new TemplateString("");

  public TemplateString(String value) {
    super(value);
  }

  public TemplateString concat(TemplateString other) {
    if (getValue().isEmpty()) {
      return other;
    }
    if (other.getValue().isEmpty()) {
      return this;
    }
    return new TemplateString(getValue() + other.getValue());
  }

  @Override
  public TemplateObject operation(TokenType operator, TemplateObject operand, ProcessContext context) {
    if (operator != TokenType.PLUS) {
      super.operation(operator, operand, context);
    }
    return concat(operand.evaluate(context, TemplateString.class));
  }
}
