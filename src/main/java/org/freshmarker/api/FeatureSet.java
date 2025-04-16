package org.freshmarker.api;

public interface FeatureSet {
    boolean isEnabled(TemplateFeature feature);

    boolean isDisabled(TemplateFeature feature);
}
