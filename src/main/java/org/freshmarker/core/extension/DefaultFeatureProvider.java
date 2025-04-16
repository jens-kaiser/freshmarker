package org.freshmarker.core.extension;

import org.freshmarker.api.TemplateFeature;
import org.freshmarker.api.extension.TemplateFeatureProvider;
import org.freshmarker.core.BuiltinHandlingFeature;
import org.freshmarker.core.IncludeDirectiveFeature;
import org.freshmarker.core.SwitchDirectiveFeature;

import java.util.EnumSet;
import java.util.HashSet;

import java.util.Set;

public class DefaultFeatureProvider implements TemplateFeatureProvider {
    @Override
    public Set<TemplateFeature> provideFeatures() {
        Set<TemplateFeature> templateFeatures = new HashSet<>();
        templateFeatures.addAll(EnumSet.allOf(IncludeDirectiveFeature.class));
        templateFeatures.addAll(EnumSet.allOf(SwitchDirectiveFeature.class));
        templateFeatures.addAll(EnumSet.allOf(BuiltinHandlingFeature.class));
        return templateFeatures;
    }
}
