package org.freshmarker.core.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateObject;

public class MappingTemplateObjectProvider implements TemplateObjectProvider {

  private final Map<Class<?>, Function<Object, TemplateObject>> mapper = new HashMap<>();

  @Override
  public TemplateObject provide(Environment environment, Object o) {
    Function<Object, TemplateObject> mapping = mapper.get(o.getClass());
    if (mapping != null) {
      return mapping.apply(o);
    }
    return null;
  }

  public Map<Class<?>, Function<Object, TemplateObject>> getMapper() {
    return mapper;
  }
}
