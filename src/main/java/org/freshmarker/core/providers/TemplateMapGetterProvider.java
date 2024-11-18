package org.freshmarker.core.providers;

import org.freshmarker.core.environment.BaseEnvironment;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class TemplateMapGetterProvider {
    private final Function<Class<?>, Map<String, Method>> methodSupplier;

    public TemplateMapGetterProvider(Function<Class<?>, Map<String, Method>> methodSupplier) {
        this.methodSupplier = methodSupplier;
    }

    public interface Getter {
        Object get(Object instance);
    }

    private final Map<Class<?>, Map<String, Getter>> methodBeans = new HashMap<>();

    public Map<String, Object> provide(Object bean, TemplateObjectMapper environment) {
        final Map<String, Getter> methods = methodBeans.computeIfAbsent(bean.getClass(), b -> collectGetters(bean));
        return new BaseGetterMap(methods, environment, bean);
    }

    private Map<String, Getter> collectGetters(Object bean) {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        Map<String, Getter> result = new HashMap<>();
        for (Entry<String, Method> entry : methodSupplier.apply(bean.getClass()).entrySet()) {
            result.put(entry.getKey(), wrapGetter(lookup, entry.getValue()));
        }
        return result;
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
