package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

import java.util.List;

public class TemplateSequenceLooper extends AbstractTemplateLooper<Object> {

  public TemplateSequenceLooper(List<Object> sequence) {
    super(sequence);
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
      if (current != null) {
          return current;
      }
      Object object = sequence.get(index);
      if (object instanceof TemplateObject templateObject) {
        current = templateObject;
        return templateObject;
      }
      current = context.mapObject(object);
      return current;
  }
}
