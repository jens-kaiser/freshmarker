package org.freshmarker.core.model.primitive;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.DotAddressable;
import org.freshmarker.core.model.TemplateObject;

public class TemplateEnum<E extends Enum<E>> extends TemplatePrimitive<E> implements DotAddressable {

  public TemplateEnum(E value) {
    super(value);
  }

  public TemplateObject get(ProcessContext context, String name) {
    return switch (name) {
      case "ordinal" -> TemplateNumber.of(getValue().ordinal());
      case "name" -> new TemplateString(getValue().name());
      default -> throw new ProcessException("unknown attribute: " + name);
    };
  }
}
