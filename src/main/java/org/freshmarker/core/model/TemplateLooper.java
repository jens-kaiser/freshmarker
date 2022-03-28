package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplateObject;

public class TemplateLooper implements TemplateObject {

  private final TemplateListSequence sequence;
  private int index;

  public TemplateLooper(TemplateListSequence sequence) {
    this.sequence = sequence;
  }

  @Override
  public String evaluate(Environment environment) {
    return environment.getFormatter(getClass()).format(evaluateToObject(environment), environment.getLocale());
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    return sequence.get(environment, index);
  }

  public TemplateNumber getIndex() {
    return new TemplateNumber(index, Type.INTEGER);
  }

  public TemplateBoolean isFirst() {
    return index == 0 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
  }

  public TemplateBoolean isLast() {
    return sequence.size().getValue().equals(index + 1) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
  }

  public void increment() {
    index++;
  }
}
