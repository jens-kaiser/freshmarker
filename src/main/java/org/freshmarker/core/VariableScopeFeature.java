package org.freshmarker.core;

import org.freshmarker.api.TemplateFeature;

@Deprecated(forRemoval = true)
public enum VariableScopeFeature implements TemplateFeature {
    /**
     * Feature that determines whether each block can create a new variable context.
     *<p>
     * The default value is 'false', meaning that variable contexts con only created by macro, conditional, and list directives.
     *<p>
     * The feature is disabled by default.
     * @deprecated replaced by {@link SystemFeature#ALL_BLOCKS}
     */
    @Deprecated
    ALL_BLOCKS;

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
