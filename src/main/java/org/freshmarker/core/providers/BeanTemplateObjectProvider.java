package org.freshmarker.core.providers;

import org.freshmarker.core.Environment;
import org.freshmarker.core.UnsupportedDataTypeException;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateBeanProvider;
import org.freshmarker.core.model.TemplateObject;

public class BeanTemplateObjectProvider implements TemplateObjectProvider {

    private final TemplateBeanProvider beanProvider = new TemplateBeanProvider();

    @Override
    public TemplateObject provide(Environment environment, Object o) {
        Class<?> type = o.getClass();
        if (type.isPrimitive()) {
            throw new UnsupportedDataTypeException("unsupported primitive: " + type);
        }
        String name = type.getName();
        if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun.") || name.startsWith("com.sun.")) {
            throw new UnsupportedDataTypeException("unsupported system class: " + type);
        }
        return new TemplateBean(beanProvider.provide(o, environment), type);
    }
}
