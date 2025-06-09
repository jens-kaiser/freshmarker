package org.freshmarker.core.providers;

import org.freshmarker.core.ModelSecurityGateway.ModelSecurityHandler;
import org.freshmarker.core.model.TemplateBean;

import java.util.Map;

public class RecordTemplateObjectProvider implements TemplateObjectProvider {

    private final TemplateMapGetterProvider getterProvider;

    private final ModelSecurityHandler modelSecurityHandler;

    public RecordTemplateObjectProvider(ModelSecurityHandler modelSecurityHandler) {
        this.modelSecurityHandler = modelSecurityHandler;
        getterProvider = new TemplateMapGetterProvider(new RecordMethodProvider());
    }

    @Override
    public TemplateBean provide(TemplateObjectMapper environment, Object o) {
        Class<?> type = o.getClass();
        if (!type.isRecord()) {
            return null;
        }
        modelSecurityHandler.check(type);
        Map<String, Object> map = getterProvider.provide(o, environment);
        return new TemplateBean(map, o.getClass());
    }
}
