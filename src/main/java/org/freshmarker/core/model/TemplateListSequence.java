package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.ProcessContext;

public class TemplateListSequence implements TemplateSequence {

  private final List<Object> sequence;

  public TemplateListSequence(List<Object> sequence) {
    this.sequence = sequence;
  }

  @Override
  public TemplateObject get(ProcessContext context, int index) {
    return context.mapObject(sequence.get(index));
  }

  @Override
  public int size(ProcessContext context) {
    return sequence.size();
  }

  public TemplateListSequence slice(int min, int max) {
    return new TemplateListSequence(sequence.subList(min, max));
  }

  public TemplateListSequence slice(int min) {
    return new TemplateListSequence(sequence.subList(min, sequence.size()));
  }

  @Override
  public TemplateListSequence evaluateToObject(ProcessContext context) {
    return this;
  }

  public List<Object> getSequence(ProcessContext context) {
    return sequence;
  }
}
