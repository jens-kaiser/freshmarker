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
    ALLOW_ONLY_EQUAL_TYPE_ONS,

    /**
     * Feature that determines whether the switch optimized with a map.
     *<p>
     * The default value is 'false', meaning that the optimization is disabled.
     *<p>
     * The feature is disabled by default.
     */
    OPTIMIZE_CONSTANT_SWITCH,

    /**
     * Feature that determines whether a duplicate case expression results in an error.
     *<p>
     * The default value is 'false', meaning that duplicate case expression does not result in an error.
     *<p>
     * The feature is disabled by default.
     */
    ERROR_ON_DUPLICATE_CASE_EXPRESSION;

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
