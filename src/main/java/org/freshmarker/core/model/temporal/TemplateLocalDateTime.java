package org.freshmarker.core.model.temporal;

import java.time.LocalDateTime;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateRelational.Relation;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateLocalDateTime extends TemplatePrimitive<LocalDateTime> implements TemplateDateTime {
  public TemplateLocalDateTime(LocalDateTime value) {
    super(value);
  }

  @Override
  public TemplatePrimitive<?> relational(Relation operator, TemplatePrimitive<?> operand, ProcessContext context) {
    TemplateLocalDateTime rightValue = (TemplateLocalDateTime)operand;
    return compareValues(operator, getValue().compareTo(rightValue.getValue()));
  }
}
