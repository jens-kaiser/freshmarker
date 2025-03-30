package org.freshmarker.api;

import org.freshmarker.core.features.TemplateFeature;

import java.util.List;

public interface TemplateFeatureProvider extends Extension {
    List<TemplateFeature> provideFeatures();
}
