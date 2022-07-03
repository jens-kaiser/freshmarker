package org.freshmarker.core.model.primitive;

import org.freshmarker.core.model.TemplateBooleanExpression;
import org.freshmarker.core.model.TemplateObject;

public class TemplateBoolean extends TemplatePrimitive<Boolean> implements TemplateBooleanExpression {

  public static final TemplateBoolean TRUE = new TemplateBoolean(true);
  public static final TemplateBoolean FALSE = new TemplateBoolean(false);

  public static TemplateBoolean from(boolean value) {
    return value ? TRUE : FALSE;
  }

  private TemplateBoolean(Boolean value) {
    super(value);
  }

  @Override
  public TemplateObject not() {
    return this == TRUE ? FALSE : TRUE;
  }
}
