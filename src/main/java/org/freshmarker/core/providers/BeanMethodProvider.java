package org.freshmarker.core.providers;

import org.freshmarker.core.ProcessException;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class BeanMethodProvider implements Function<Class<?>, Map<String, Method>> {
    @Override
    public Map<String, Method> apply(Class<?> type) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
            return Stream.of(beanInfo.getPropertyDescriptors()).collect(toMap(FeatureDescriptor::getName, PropertyDescriptor::getReadMethod));
        } catch (IntrospectionException e) {
            throw new ProcessException(e.getMessage(), e);
        }
    }
}
