package org.freshmarker.core.model;

import org.freshmarker.core.Environment;

public class TemplateNull implements TemplateObject {

  public static final TemplateNull NULL = new TemplateNull();

  private TemplateNull() {
    super();
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    return this;
  }
}
