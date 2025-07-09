package org.freshmarker.core.model.temporal;

import java.time.Duration;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateDuration extends TemplatePrimitive<Duration> {

  public TemplateDuration(Duration value) {
    super(value);
  }

  @Override
  public TemplatePrimitive<?> relational(Token.TokenType operator, TemplatePrimitive<?> operand, ProcessContext context) {
    TemplateDuration rightValue = (TemplateDuration)operand;
    return compareValues(operator, getValue().compareTo(rightValue.getValue()));
  }
}
