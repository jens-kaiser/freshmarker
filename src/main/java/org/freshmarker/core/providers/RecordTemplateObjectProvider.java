package org.freshmarker.core.providers;

import org.freshmarker.Configuration.FeatureFlag;
import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.RecordMethodProvider;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateMapGetterProvider;
import org.freshmarker.core.model.TemplateMapReflectionsProvider;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;

public class RecordTemplateObjectProvider implements TemplateObjectProvider {

    private final TemplateMapReflectionsProvider reflectionsProvider;
    private final TemplateMapGetterProvider getterProvider;

    private final ModelSecurityGateway modelSecurityGateway;
    private final FeatureFlag featureFlag;

    public RecordTemplateObjectProvider(FeatureFlag featureFlag, ModelSecurityGateway modelSecurityGateway) {
        this.featureFlag = featureFlag;
        this.modelSecurityGateway = modelSecurityGateway;
        RecordMethodProvider methodSupplier = new RecordMethodProvider();
        reflectionsProvider = new TemplateMapReflectionsProvider(methodSupplier);
        getterProvider = new TemplateMapGetterProvider(methodSupplier);
    }

    @Override
    public TemplateObject provide(BaseEnvironment environment, Object o) {
        Class<?> type = o.getClass();
        if (!type.isRecord()) {
            return null;
        }
        if (!environment.getChecks().contains(type)) {
            modelSecurityGateway.check(type);
        }
        environment.getChecks().add(type);
        Map<String, Object> map = featureFlag == FeatureFlag.REFLECTIONS ? reflectionsProvider.provide(o, environment) : getterProvider.provide(o, environment);
        return new TemplateBean(map, o.getClass());
    }
}
