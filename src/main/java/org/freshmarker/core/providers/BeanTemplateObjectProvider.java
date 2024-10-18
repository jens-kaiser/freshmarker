package org.freshmarker.core.providers;

import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateBean;

import java.util.Map;

public class BeanTemplateObjectProvider implements TemplateObjectProvider {

    private final TemplateMapGetterProvider beanGetterProvider;

    private final ModelSecurityGateway modelSecurityGateway;

    public BeanTemplateObjectProvider(ModelSecurityGateway modelSecurityGateway) {
        this.modelSecurityGateway = modelSecurityGateway;
        BeanMethodProvider methodSupplier = new BeanMethodProvider();
        beanGetterProvider = new TemplateMapGetterProvider(methodSupplier);
    }

    @Override
    public TemplateBean provide(BaseEnvironment environment, Object o) {
        Class<?> type = o.getClass();
        if (!environment.getChecks().contains(type)) {
            modelSecurityGateway.check(type);
        }
        environment.getChecks().add(type);
        Map<String, Object> map = beanGetterProvider.provide(o, environment);
        return new TemplateBean(map, type);
    }
}
