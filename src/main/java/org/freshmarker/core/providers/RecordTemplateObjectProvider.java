package org.freshmarker.core.providers;

import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateBean;

import java.util.Map;

public class RecordTemplateObjectProvider implements TemplateObjectProvider {

    private final TemplateMapGetterProvider getterProvider;

    private final ModelSecurityGateway modelSecurityGateway;

    public RecordTemplateObjectProvider(ModelSecurityGateway modelSecurityGateway) {
        this.modelSecurityGateway = modelSecurityGateway;
        RecordMethodProvider methodSupplier = new RecordMethodProvider();
        getterProvider = new TemplateMapGetterProvider(methodSupplier);
    }

    @Override
    public TemplateBean provide(BaseEnvironment environment, Object o) {
        Class<?> type = o.getClass();
        if (!type.isRecord()) {
            return null;
        }
        if (!environment.getChecks().contains(type)) {
            modelSecurityGateway.check(type);
        }
        environment.getChecks().add(type);
        Map<String, Object> map = getterProvider.provide(o, environment);
        return new TemplateBean(map, o.getClass());
    }
}
