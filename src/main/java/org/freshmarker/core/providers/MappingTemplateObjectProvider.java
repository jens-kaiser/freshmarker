package org.freshmarker.core.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.freshmarker.core.TypeMapper;
import org.freshmarker.core.model.TemplateObject;

public class MappingTemplateObjectProvider implements TemplateObjectProvider {

  private final Map<Class<?>, Function<Object, TemplateObject>> mapper = new HashMap<>();

  @Override
  public TemplateObject provide(TemplateObjectMapper environment, Object o) {
    Function<Object, TemplateObject> mapping = mapper.get(o.getClass());
    if (mapping != null) {
      return mapping.apply(o);
    }
    return null;
  }

  public void register(Map<Class<?>,TypeMapper> mappings) {
    mapper.putAll(mappings);
  }

  public void addMapper(Class<?> type, Function<Object, TemplateObject> mapping) {
    mapper.put(Objects.requireNonNull(type), Objects.requireNonNull(mapping));
  }

  public MappingTemplateObjectProvider copy() {
    MappingTemplateObjectProvider copy = new MappingTemplateObjectProvider();
    copy.mapper.putAll(mapper);
    return copy;
  }
}
