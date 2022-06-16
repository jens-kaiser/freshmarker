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
    final Map<String, Method> methods = getMethodMap(bean);
    return new AbstractMap<>() {
      @Override
      public Set<Entry<String, Object>> entrySet() {
        return new AbstractSet<>() {
          @Override
          public int size() {
            return methods.size();
          }

          @Override
          public Iterator<Entry<String, Object>> iterator() {
            return new Iterator<>() {
              private final Iterator<Entry<String, Method>> iter = methods.entrySet().iterator();

              @Override
              public void remove() {
                throw new UnsupportedOperationException("remove() is not supported");
              }

              @Override
              public Entry<String, Object> next() {
                final Entry<String, Method> e = iter.next();
                final Method m = e.getValue();

                return new Entry<>() {
                  @Override
                  public String getKey() {
                    return e.getKey();
                  }

                  @Override
                  public Object getValue() {
                    try {
                      return environment.mapObject(m.invoke(bean));
                    } catch (InvocationTargetException ite) {
                      throw new IllegalArgumentException(ite.getTargetException());
                    } catch (IllegalAccessException iae) {
                      throw new IllegalArgumentException(iae);
                    }
                  }

                  @Override
                  public Object setValue(Object value) {
                    throw new UnsupportedOperationException("setValue() is not supported");
                  }
                };
              }

              @Override
              public boolean hasNext() {
                return iter.hasNext();
              }
            };
          }
        };
      }
    };
  }

  private Map<String, Method> getMethodMap(Object bean) {
    return beans.computeIfAbsent(bean.getClass(), b -> collectMethods(bean));
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
