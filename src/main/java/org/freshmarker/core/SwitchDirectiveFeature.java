package org.freshmarker.core;

import org.freshmarker.api.TemplateFeature;

public enum SwitchDirectiveFeature implements TemplateFeature {
    /**
     * Feature that determines whether case expressions must be constants.
     *<p>
     * The default value is 'false', meaning that expressions can contain variables.
     *<p>
     * The feature is disabled by default.
     */
    ALLOW_ONLY_CONSTANT_CASES,
    /**
     * Feature that determines whether on expressions must be constants.
     *<p>
     * The default value is 'false', meaning that expressions can contain variables
     *<p>
     * The feature is disabled by default.
     */
    ALLOW_ONLY_CONSTANT_ONS,
    /**
     * Feature that determines whether all case expressions must contain constants from the same type.
     *<p>
     * The default value is 'false', meaning that expressions can match different types.
     *<p>
     * The feature is disabled by default.
     */
    ALLOW_ONLY_EQUAL_TYPE_CASES,
    /**
     * Feature that determines whether all on expressions must contain constants from the same type.
     *<p>
     * The default value is 'false', meaning that expressions can match different types.
     *<p>
     * The feature is disabled by default.
     */
    ALLOW_ONLY_EQUAL_TYPE_ONS;

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
