package org.freshmarker.core;

import org.freshmarker.api.TemplateFeature;

public enum SystemFeature implements TemplateFeature {
    /**
     * Feature that enables locale sensitive String compare.
     *<p>
     * The default value is 'false', meaning that String instances are compared not locale sensitive
     *<p>
     * The feature is disabled by default.
     */
    LOCALE_SENSITIVE_STRING_COMPARE,

    /**
     * Feature that enables Set instances as sequences.
     * This does not affect sets that are recognized as `SequencedCollection`.
     *<p>
     * The default value is 'false', meaning that Set are not used as sequences
     *<p>
     * The feature is disabled by default.
     */
    SET_AS_SEQUENCE,

    /**
     * Feature that enables Collection instances as sequences.
     * This does not affect collections that are recognized as `SequencedCollection`.
     *<p>
     * The default value is 'false', meaning that Collections are not used as sequences
     *<p>
     * The feature is disabled by default.
     */
    COLLECTION_AS_SEQUENCE;

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
