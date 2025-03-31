package org.freshmarker.api.extension;

import org.freshmarker.core.features.FeatureSet;

/**
 * Marker interface for all FreshMarker extensions
 */
public interface Extension {
    /**
     * This method can be used, to modify this {@link Extension} by a feature from the current feature set.
     *
     * @param featureSet the current feature set
     */
    default void init(FeatureSet featureSet) {

    }
}
