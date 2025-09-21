package org.freshmarker.core.model.temporal;

import java.time.Duration;

import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateRelational.Relation;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateDuration extends TemplatePrimitive<Duration> {

  public TemplateDuration(Duration value) {
    super(value);
  }

  @Override
  public TemplatePrimitive<?> relational(Relation operator, TemplatePrimitive<?> operand, ProcessContext context) {
    TemplateDuration rightValue = (TemplateDuration)operand;
    return compareValues(operator, getValue().compareTo(rightValue.getValue()));
  }

    @Override
    public TemplateDuration negate() {
        return new TemplateDuration(getValue().negated());
    }
}
