package org.freshmarker.core.model;

import org.freshmarker.core.model.primitive.TemplateObject;

public class TemplateNull implements TemplateObject {

  public static final TemplateNull NULL = new TemplateNull();

  private TemplateNull() {
    super();
  }
}
