package org.freshmarker.core.model.temporal;

import java.time.LocalTime;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateLocalTime extends TemplatePrimitive<LocalTime> implements TemplateTime {

  public TemplateLocalTime(LocalTime value) {
    super(value);
  }

  @Override
  public TemplatePrimitive<?> relational(Token.TokenType operator, TemplatePrimitive<?> operand, ProcessContext context) {
    TemplateLocalTime rightValue = (TemplateLocalTime)operand;
    return compareValues(operator, getValue().compareTo(rightValue.getValue()));
  }
}
