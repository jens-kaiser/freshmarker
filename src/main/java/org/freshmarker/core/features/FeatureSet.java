package org.freshmarker.core.features;

public interface FeatureSet {
    boolean isEnabled(TemplateFeature feature);

    boolean isDisabled(TemplateFeature feature);
}
