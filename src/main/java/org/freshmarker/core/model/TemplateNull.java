package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateObject;

public class TemplateNull implements TemplateObject {

  public static final TemplateNull NULL = new TemplateNull();

  private TemplateNull() {
    super();
  }

  @Override
  public String evaluate(Environment environment) {
    return null;
  }
}
