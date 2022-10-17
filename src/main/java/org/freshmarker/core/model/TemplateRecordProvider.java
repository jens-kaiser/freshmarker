package org.freshmarker.core.model;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.freshmarker.core.Environment;

public class TemplateRecordProvider {

  private final Map<Class<?>, Map<String, Method>> beans = new HashMap<>();

  public Map<String, Object> provide(Object bean, Environment environment) {
    final Map<String, Method> methods = beans.computeIfAbsent(bean.getClass(), b -> collectMethods(bean));
    beans.put(bean.getClass(), methods);
    return new AbstractReflectionsMap(methods, environment, bean);
  }

  private Map<String, Method> collectMethods(Object bean) {
    return Stream.of(bean.getClass().getRecordComponents())
        .collect(toMap(RecordComponent::getName, RecordComponent::getAccessor));
  }
}
