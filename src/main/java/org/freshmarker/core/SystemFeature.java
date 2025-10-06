package org.freshmarker.core;

import org.freshmarker.api.TemplateFeature;

public enum SystemFeature implements TemplateFeature {
    /**
     * Feature that enables partial expression reduction.
     *<p>
     * The default value is 'false', meaning that partial expression reduction is not used
     *<p>
     * The feature is disabled by default.
     */
    PARTIAL_EXPRESSION_REDUCTION,

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
    COLLECTION_AS_SEQUENCE,

    /**
     * Feature that enables Character literals.
     * String literals with single quotes and length one become character literals.
     *<p>
     * The default value is 'false', meaning that no Character literals are available
     *<p>
     * The feature is disabled by default.
     */
    CHARACTER_LITERAL,

    /**
     * Feature that enables escape sequences.
     * Escape sequences in string literals are replaced by the corresponding character.
     *<p>
     * The default value is 'false', meaning that escape sequences are not replaced.
     *<p>
     * The feature is disabled by default.
     */
    ESCAPE_SEQUENCE,

    /**
     * Feature that enables HOUR_OF_DAY in formatter pattern.
     * So far, CLOCK_HOUR_OF_AMPM has been included in the initial patterns.
     *<p>
     * The default value is 'false', meaning that CLOCK_HOUR_OF_AMPM is used.
     *<p>
     * The feature is disabled by default.
     */
    HOUR_OF_DAY;

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
