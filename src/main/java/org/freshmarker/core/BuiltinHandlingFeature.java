package org.freshmarker.core;

import org.freshmarker.api.TemplateFeature;

public enum BuiltinHandlingFeature implements TemplateFeature {
    /**
     * Feature that ignores errors due empty optionals on built-ins.
     *<p>
     * The default value is 'false', meaning that empty optional causes errors on built-ins.
     *<p>
     * The feature is disabled by default.
     */
    IGNORE_OPTIONAL_EMPTY,

    /**
     * Feature that ignores errors due null values on built-ins.
     *<p>
     * The default value is 'false', meaning that null values causes errors on built-ins.
     *<p>
     * The feature is disabled by default.
     */
    IGNORE_NULL;

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
