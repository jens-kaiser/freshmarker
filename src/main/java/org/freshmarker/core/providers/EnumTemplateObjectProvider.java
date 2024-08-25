package org.freshmarker.core.providers;

import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateEnum;

public class EnumTemplateObjectProvider implements TemplateObjectProvider {

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public TemplateObject provide(BaseEnvironment environment, Object o) {
      return o instanceof Enum e ? new TemplateEnum<>(e) : null;
  }
}
