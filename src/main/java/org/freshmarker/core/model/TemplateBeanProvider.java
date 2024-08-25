package org.freshmarker.core.model;

import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.BaseEnvironment;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class TemplateBeanProvider {

    private final Map<Class<?>, Map<String, Method>> beans = new HashMap<>();

    public Map<String, Object> provide(Object bean, BaseEnvironment environment) {
        final Map<String, Method> methods = beans.computeIfAbsent(bean.getClass(), b -> collectMethods(bean));
        return new BaseReflectionsMap(methods, environment, bean);
    }

    private Map<String, Method> collectMethods(Object bean) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
            return Stream.of(beanInfo.getPropertyDescriptors()).collect(toMap(FeatureDescriptor::getName, PropertyDescriptor::getReadMethod));
        } catch (IntrospectionException e) {
            throw new ProcessException(e.getMessage(), e);
        }
    }
}
