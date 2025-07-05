package org.freshmarker.core.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.freshmarker.api.TypeMapper;
import org.freshmarker.core.model.TemplateObject;

public class MappingTemplateObjectProvider implements TemplateObjectProvider {

  private static final Function<Object, TemplateObject> NULL_MAPPING = o -> null;

  private final Map<Class<?>, Function<Object, TemplateObject>> mapper;

  public MappingTemplateObjectProvider() {
    mapper = new HashMap<>();
  }

  @Override
  public TemplateObject provide(TemplateObjectMapper environment, Object o) {
    return mapper.getOrDefault(o.getClass(), NULL_MAPPING).apply(o);
  }

  public void register(Map<Class<?>,TypeMapper> mappings) {
    mapper.putAll(mappings);
  }
}
