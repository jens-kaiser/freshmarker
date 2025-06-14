package org.freshmarker.core;

import org.freshmarker.api.TemplateFeature;

public enum SystemFeature implements TemplateFeature {
    /**
     * Feature that determines whether each block can create a new variable context.
     *<p>
     * The default value is 'false', meaning that variable contexts con only created by macro, conditional, and list directives.
     *<p>
     * The feature is disabled by default.
     */
    ALL_BLOCKS,

    /**
     * Feature that determines whether the index operator on string return a character or a string.
     *<p>
     * The default value is 'false', meaning index operator on string return a string.
     *<p>
     * The feature is disabled by default.
     */
    STRING_INDEX_RETURNS_CHARACTER,

    /**
     * Feature that determines whether the model classes are checked.
     *<p>
     * The default value is 'true', meaning that model variables are checked.
     *<p>
     * The feature is enabled by default.
     */
    MODEL_SECURITY;

    @Override
    public boolean isEnabledByDefault() {
        return MODEL_SECURITY == this;
    }
}
