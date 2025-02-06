package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public record TemplateSequenceLoopVariable(TemplateLooper looper) implements TemplateLoopVariable {

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return looper.evaluateToObject(context);
  }
}
