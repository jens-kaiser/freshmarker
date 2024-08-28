package org.freshmarker.core.model;

import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.BaseEnvironment;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TemplateBeanGetterProvider {
    public interface Getter {
        Object get(Object instance);
    }

    private final Map<Class<?>, Map<String, Getter>> methodBeans = new HashMap<>();

    public Map<String, Object> provide(Object bean, BaseEnvironment environment) {
        final Map<String, Getter> methods = methodBeans.computeIfAbsent(bean.getClass(), b -> collectGetters(bean));
        return new BaseGetterMap(methods, environment, bean);
    }

    private Map<String, Getter> collectGetters(Object bean) {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
            Map<String, Getter> result = new HashMap<>();
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                result.put(propertyDescriptor.getName(), wrapGetter(lookup, propertyDescriptor.getReadMethod()));
            }
            return result;
        } catch (IntrospectionException e) {
            throw new ProcessException(e.getMessage(), e);
        }
    }

    private static Getter wrapGetter(Lookup lookup, Method method) {
        try {
            MethodHandle handle = lookup.unreflect(method);
            return (Getter) LambdaMetafactory.metafactory(lookup, "get",
                    MethodType.methodType(Getter.class), MethodType.methodType(Object.class, Object.class),
                    handle, handle.type()).getTarget().invoke();
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not generate the function to access the getter " + method.getName(), e);
        }
    }
}
