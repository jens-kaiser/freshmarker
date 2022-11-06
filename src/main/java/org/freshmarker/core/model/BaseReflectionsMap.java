package org.freshmarker.core.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.freshmarker.core.Environment;

class BaseReflectionsMap extends AbstractMap<String, Object> {

  private final Map<String, Method> methods;
  private final Environment environment;
  private final Object bean;

  public BaseReflectionsMap(Map<String, Method> methods, Environment environment, Object bean) {
    this.methods = methods;
    this.environment = environment;
    this.bean = bean;
  }

  @Override
  public Object get(Object key) {
    Method m = methods.get(key);
    if (m == null) {
      return null;
    }
    try {
      return environment.mapObject(m.invoke(bean));
    } catch (InvocationTargetException ite) {
      throw new IllegalArgumentException(ite.getTargetException());
    } catch (IllegalAccessException iae) {
      throw new IllegalArgumentException(iae);
    }
  }

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
}
