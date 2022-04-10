package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public class TemplateLooper implements TemplateObject {

  private final TemplateSequence sequence;
  private final int size;
  private int index;

  public TemplateLooper(TemplateSequence sequence, int size) {
    this.sequence = sequence;
    this.size = size;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return sequence.get(context, index);
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

  public TemplateObject cycle(List<TemplateObject> cycle) {
    return cycle.get(index % cycle.size());
  }
}
