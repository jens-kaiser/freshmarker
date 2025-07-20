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
    LOCALE_SENSITIVE_STRING_COMPARE;

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
