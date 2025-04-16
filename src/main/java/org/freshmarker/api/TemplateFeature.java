package org.freshmarker.api;

public interface TemplateFeature {
    default boolean isEnabledByDefault() {
        return true;
    }
}
