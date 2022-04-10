package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public class TemplateNull implements TemplateObject {

  public static final TemplateNull NULL = new TemplateNull();

  private TemplateNull() {
    super();
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return this;
  }
}
