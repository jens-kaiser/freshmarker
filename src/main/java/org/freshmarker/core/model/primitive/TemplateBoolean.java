package org.freshmarker.core.model.primitive;

public class TemplateBoolean extends TemplatePrimitive<Boolean> {

  public static final TemplateBoolean TRUE = new TemplateBoolean(true);
  public static final TemplateBoolean FALSE = new TemplateBoolean(false);

  public static TemplateBoolean from(boolean value) {
    return value ? TRUE : FALSE;
  }

  private TemplateBoolean(Boolean value) {
    super(value);
  }

  public TemplateBoolean not() {
    return this == TRUE ? FALSE : TRUE;
  }
}
