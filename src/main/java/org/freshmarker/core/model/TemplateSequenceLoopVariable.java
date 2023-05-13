package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public class TemplateSequenceLoopVariable implements TemplateLoopVariable {

  private final TemplateLooper looper;

  public TemplateSequenceLoopVariable(TemplateLooper looper) {
    this.looper = looper;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return looper.evaluateToObject(context);
  }
}
