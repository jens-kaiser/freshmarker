package org.freshmarker.api.extension;

import java.util.Set;

/**
 * An {@link Extension} to add new template feature.
 */
public interface TemplateFeatureProvider extends Extension {
    /**
     * Returns a set of template features
     * @return a set of template features
     */
    Set<TemplateFeature> provideFeatures();
}
