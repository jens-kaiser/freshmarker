package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

import java.util.List;

public class TemplateSequenceLooper extends AbstractTemplateLooper<Object> {

  public TemplateSequenceLooper(List<Object> sequence) {
    super(sequence);
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    Object object = sequence.get(index);
    if (object instanceof TemplateObject templateObject) {
      return templateObject;
    }
    return context.mapObject(object);
  }
}
