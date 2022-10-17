package org.freshmarker.core.model;

import static java.util.stream.Collectors.toMap;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;

public class TemplateBeanProvider {

  private final Map<Class<?>, Map<String, Method>> beans = new HashMap<>();

  public Map<String, Object> provide(Object bean, Environment environment) {
    final Map<String, Method> methods = beans.computeIfAbsent(bean.getClass(), b -> collectMethods(bean));
    return new AbstractReflectionsMap(methods, environment, bean);
  }

  private Map<String, Method> collectMethods(Object bean) {
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
      return Stream.of(beanInfo.getPropertyDescriptors()).collect(toMap(
          FeatureDescriptor::getName, PropertyDescriptor::getReadMethod));
    } catch (IntrospectionException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }
}
