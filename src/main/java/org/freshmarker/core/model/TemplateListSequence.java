package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public class TemplateListSequence implements TemplateSequence {

  private final List<Object> sequence;

  public TemplateListSequence(List<Object> sequence) {
    this.sequence = sequence;
  }

  @Override
  public TemplateObject get(ProcessContext context, int index) {
    return context.getEnvironment().mapObject(sequence.get(index));
  }

  @Override
  public TemplateNumber size(ProcessContext context) {
    return new TemplateNumber(sequence.size(), Type.INTEGER);
  }

  public TemplateListSequence slice(int min, int max) {
    return new TemplateListSequence(sequence.subList(min, max));
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return this;
  }
}
