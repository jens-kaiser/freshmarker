package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

import java.util.List;

public class TemplateSequenceLooper extends AbstractTemplateLooper<Object> {

  public TemplateSequenceLooper(List<Object> sequence, int size) {
    super(sequence, size);
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return context.getEnvironment().mapObject(sequence.get(index));
  }
}
