package org.freshmarker.core.providers;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateBeanProvider;
import org.freshmarker.core.model.TemplateObject;

public class BeanTemplateObjectProvider implements TemplateObjectProvider {

    private final TemplateBeanProvider beanProvider = new TemplateBeanProvider();

    private final ModelSecurityGateway modelSecurityGateway;

    public BeanTemplateObjectProvider(ModelSecurityGateway modelSecurityGateway) {
        this.modelSecurityGateway = modelSecurityGateway;
    }

    @Override
    public TemplateObject provide(Environment environment, Object o) {
        Class<?> type = o.getClass();
        modelSecurityGateway.check(type);
        return new TemplateBean(beanProvider.provide(o, environment), type);
    }
}
