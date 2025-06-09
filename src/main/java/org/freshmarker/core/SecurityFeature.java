package org.freshmarker.core;

import org.freshmarker.api.TemplateFeature;

public enum SecurityFeature implements TemplateFeature {
    /**
     * Feature that determines whether the model classes are checked.
     *<p>
     * The default value is 'true', meaning that model variables are checked.
     *<p>
     * The feature is disabled by default.
     */
    MODEL_SECURITY;
}
