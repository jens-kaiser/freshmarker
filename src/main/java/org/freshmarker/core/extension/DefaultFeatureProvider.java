package org.freshmarker.core.extension;

import org.freshmarker.api.TemplateFeatureProvider;
import org.freshmarker.core.BuiltinHandlingFeature;
import org.freshmarker.core.IncludeDirectiveFeature;
import org.freshmarker.core.SwitchDirectiveFeature;
import org.freshmarker.core.features.TemplateFeature;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class DefaultFeatureProvider implements TemplateFeatureProvider {
    @Override
    public List<TemplateFeature> provideFeatures() {
        List<TemplateFeature> templateFeatures = new ArrayList<>();
        templateFeatures.addAll(EnumSet.allOf(IncludeDirectiveFeature.class));
        templateFeatures.addAll(EnumSet.allOf(SwitchDirectiveFeature.class));
        templateFeatures.addAll(EnumSet.allOf(BuiltinHandlingFeature.class));
        return templateFeatures;
    }
}
