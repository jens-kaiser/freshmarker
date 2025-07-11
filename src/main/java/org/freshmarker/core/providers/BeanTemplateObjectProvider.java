package org.freshmarker.core.providers;

import org.freshmarker.core.model.TemplateBean;

public class BeanTemplateObjectProvider implements TemplateObjectProvider {

    private final TemplateMapGetterProvider beanGetterProvider;

    public BeanTemplateObjectProvider() {
        beanGetterProvider = new TemplateMapGetterProvider(new BeanMethodProvider());
    }

    @Override
    public TemplateBean provide(TemplateObjectMapper environment, Object o) {
        return new TemplateBean(beanGetterProvider.provide(o, environment), o.getClass());
    }
}
