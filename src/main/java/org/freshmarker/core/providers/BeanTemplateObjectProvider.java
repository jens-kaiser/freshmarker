package org.freshmarker.core.providers;

import org.freshmarker.core.model.TemplateBean;

import java.util.Map;

public class BeanTemplateObjectProvider implements TemplateObjectProvider {

    private final TemplateMapGetterProvider beanGetterProvider;

    public BeanTemplateObjectProvider() {
        BeanMethodProvider methodSupplier = new BeanMethodProvider();
        beanGetterProvider = new TemplateMapGetterProvider(methodSupplier);
    }

    @Override
    public TemplateBean provide(TemplateObjectMapper environment, Object o) {
        Class<?> type = o.getClass();
        environment.getChecks().add(type);
        Map<String, Object> map = beanGetterProvider.provide(o, environment);
        return new TemplateBean(map, type);
    }
}
