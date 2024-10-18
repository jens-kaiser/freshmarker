package org.freshmarker.core.providers;

import org.freshmarker.core.environment.BaseEnvironment;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @deprecated in favor of the {@link TemplateMapGetterProvider}
 */
@Deprecated(since = "1.6.3", forRemoval = true)
public class TemplateMapReflectionsProvider {

    private final Map<Class<?>, Map<String, Method>> methodBeans = new HashMap<>();
    private final Function<Class<?>, Map<String, Method>> methodSupplier;

    public TemplateMapReflectionsProvider(Function<Class<?>, Map<String, Method>> methodSupplier) {
        this.methodSupplier = methodSupplier;
    }

    public Map<String, Object> provide(Object bean, BaseEnvironment environment) {
        final Map<String, Method> methods = methodBeans.computeIfAbsent(bean.getClass(), b -> methodSupplier.apply(bean.getClass()));
        return new BaseReflectionsMap(methods, environment, bean);
    }

}
