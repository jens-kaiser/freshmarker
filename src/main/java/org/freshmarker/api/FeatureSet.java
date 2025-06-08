package org.freshmarker.api;

import java.util.Optional;

public interface FeatureSet {
    boolean isEnabled(TemplateFeature feature);

    boolean isDisabled(TemplateFeature feature);

    Optional<Object> getConfigured(TemplateFeature feature);
}
