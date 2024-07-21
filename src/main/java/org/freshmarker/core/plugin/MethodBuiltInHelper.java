package org.freshmarker.core.plugin;

import org.freshmarker.core.ConfigurationException;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.buildin.MethodBuiltIn;
import org.freshmarker.core.model.TemplateObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

public class MethodBuiltInHelper {

    public void registerBuiltIns(PluginProvider provider, Map<BuiltInKey, BuiltIn> builtIns) {
        Arrays.stream(provider.getClass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(BuiltInMethod.class))
                .forEach(m -> registerBuiltIn(m, builtIns));
    }

    private void registerBuiltIn(Method method, Map<BuiltInKey, BuiltIn> builtIns) {
        validateBuiltInMethod(method);

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (!TemplateObject.class.isAssignableFrom(parameterTypes[0])) {
            throw new ConfigurationException("first parameter must be assignable from TemplateObject");
        }
        if (parameterTypes.length == 1) {
            getMethodBuiltIn(method, builtIns, false, false);
            return;
        }
        boolean withEnvironment = ProcessContext.class.equals(parameterTypes[1]);
        if (parameterTypes.length == 2 && withEnvironment) {
            getMethodBuiltIn(method, builtIns, true, false);
            return;
        }
        int firstBuiltInParameter = withEnvironment ? 2 : 1;
        for (int i = firstBuiltInParameter; i < parameterTypes.length - 1; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (!TemplateObject.class.isAssignableFrom(parameterType)) {
                throw new ConfigurationException("builtin parameter " + (i - 2) + " must be assignable from TemplateObject");
            }
        }
        Class<?> parameterType = parameterTypes[parameterTypes.length - 1];
        if (TemplateObject.class.isAssignableFrom(parameterType)) {
            getMethodBuiltIn(method, builtIns, withEnvironment, false);
        } else if ((parameterType.isArray() && TemplateObject.class.isAssignableFrom(parameterType.getComponentType()))) {
            getMethodBuiltIn(method, builtIns, withEnvironment, true);
        } else {
            throw new ConfigurationException("builtin parameter " + (parameterTypes.length - 3) + " must be assignable from TemplateObject");
        }
    }

    private void validateBuiltInMethod(Method method) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new ConfigurationException("builtin method must be static");
        }
        if (method.getParameterCount() == 0) {
            throw new ConfigurationException("builtin method must have parameter");
        }
        if (!TemplateObject.class.isAssignableFrom(method.getReturnType())) {
            throw new ConfigurationException("result must be assignable from TemplateObject");
        }
    }

    private void getMethodBuiltIn(Method method, Map<BuiltInKey, BuiltIn> builtIns, boolean withEnvironment,
                                  boolean withVarargs) {
        String annotatedName = method.getAnnotation(BuiltInMethod.class).value();
        Class<?>[] parameterTypes = method.getParameterTypes();
        @SuppressWarnings("unchecked")
        Class<? extends TemplateObject> parameterType = (Class<? extends TemplateObject>) parameterTypes[0];
        MethodBuiltIn methodBuiltIn = new MethodBuiltIn(method, withEnvironment, withVarargs);
        if (!annotatedName.isEmpty()) {
            builtIns.put(new BuiltInKey(parameterType, annotatedName), methodBuiltIn);
        } else {
            String name = method.getName();
            builtIns.put(new BuiltInKey(parameterType, name), methodBuiltIn);
            builtIns.put(new BuiltInKey(parameterType, generateSnakeCase(name)), methodBuiltIn);
        }
    }

    private String generateSnakeCase(String name) {
        StringBuilder result = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append("_").append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
