package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public class TemplateLoopValue implements TemplateObject {
private final TemplateLooper looper;

  public TemplateLoopValue(TemplateLooper looper) {
    this.looper = looper;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return looper.evaluateToObject(context);
  }
}
