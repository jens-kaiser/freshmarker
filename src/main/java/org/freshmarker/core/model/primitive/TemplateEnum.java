package org.freshmarker.core.model.primitive;

public class TemplateEnum<E extends Enum<E>> extends TemplatePrimitive<E> {

  public TemplateEnum(E value) {
    super(value);
  }
}
