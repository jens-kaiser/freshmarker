package org.freshmarker.core.model.primitive;

public class TemplateBoolean extends TemplatePrimitive<Boolean> {

  public static final TemplateBoolean TRUE = new TemplateBoolean(true);
  public static final TemplateBoolean FALSE = new TemplateBoolean(false);

  private TemplateBoolean(Boolean value) {
    super(value);
  }
}
