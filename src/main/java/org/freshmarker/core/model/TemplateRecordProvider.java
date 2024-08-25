package org.freshmarker.core.model;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.freshmarker.core.environment.BaseEnvironment;

public class TemplateRecordProvider {

  private final Map<Class<?>, Map<String, Method>> records = new HashMap<>();

  public Map<String, Object> provide(Object recordObject, BaseEnvironment environment) {
    Map<String, Method> methods = records.computeIfAbsent(recordObject.getClass(), b -> collectMethods(recordObject));
    return new BaseReflectionsMap(methods, environment, recordObject);
  }

  private Map<String, Method> collectMethods(Object recordObject) {
    return Stream.of(recordObject.getClass().getRecordComponents())
        .collect(toMap(RecordComponent::getName, RecordComponent::getAccessor));
  }
}
