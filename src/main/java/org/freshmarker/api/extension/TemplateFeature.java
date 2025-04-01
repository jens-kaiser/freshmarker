package org.freshmarker.api.extension;

public interface TemplateFeature {
    default boolean isEnabledByDefault() {
        return true;
    }
}
