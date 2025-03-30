package org.freshmarker.api;

import org.freshmarker.core.features.TemplateFeature;

import java.util.List;

/**
 * An {@link Extension} to add new template feature.
 */
public interface TemplateFeatureProvider extends Extension {
    /**
     * Returns a list of template features
     * @return a list of template features
     */
    List<TemplateFeature> provideFeatures();
}
