package org.freshmarker.api;

import org.freshmarker.api.extension.TemplateFeature;

public interface FeatureSet {
    boolean isEnabled(TemplateFeature feature);

    boolean isDisabled(TemplateFeature feature);
}
