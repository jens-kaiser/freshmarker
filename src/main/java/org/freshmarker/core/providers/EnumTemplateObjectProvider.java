package org.freshmarker.core.providers;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateEnum;

public class EnumTemplateObjectProvider implements TemplateObjectProvider {

  @Override
  public TemplateObject provide(Environment environment, Object o) {
    if (o instanceof Enum<?>) {
      return new TemplateEnum<>((Enum) o);
    }
    return null;
  }
}
