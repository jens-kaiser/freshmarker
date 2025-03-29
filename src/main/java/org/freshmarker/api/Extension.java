package org.freshmarker.api;

import org.freshmarker.core.features.FeatureSet;

public interface Extension {
    default void init(FeatureSet featureSet) {

    }
}
