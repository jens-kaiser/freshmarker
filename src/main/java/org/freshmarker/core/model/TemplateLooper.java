package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateLooper implements TemplateObject {

  private final TemplateSequence sequence;
  private final int size;
  private int index;

  public TemplateLooper(TemplateSequence sequence, int size) {
    this.sequence = sequence;
    this.size = size;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    return sequence.get(environment, index);
  }

  public TemplateNumber getIndex() {
    return new TemplateNumber(index, Type.INTEGER);
  }

  public TemplateNumber getCounter() {
    return new TemplateNumber(index + 1, Type.INTEGER);
  }

  public TemplateBoolean isFirst() {
    return index == 0 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
  }

  public TemplateBoolean isLast() {
    return size == index + 1 ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
  }

  public TemplateBoolean hasNext() {
    return isLast().not();
  }

  public void increment() {
    index++;
  }

  public TemplateString cycle(List<TemplateString> cycle) {
    return cycle.get(index % cycle.size());
  }
}
