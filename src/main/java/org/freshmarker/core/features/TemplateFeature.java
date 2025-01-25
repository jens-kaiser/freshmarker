package org.freshmarker.core.features;

public interface TemplateFeature {
    default boolean isEnabledByDefault() {
        return true;
    }
}
