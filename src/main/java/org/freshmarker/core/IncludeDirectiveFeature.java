package org.freshmarker.core;

import org.freshmarker.api.TemplateFeature;

import java.util.Set;

public enum IncludeDirectiveFeature implements TemplateFeature {
    /**
     * Feature that determines whether include directives are evaluated.
     *<p>
     * The default value is 'false', meaning that include directives are ignored.
     *<p>
     * The feature is disabled by default.
     */
    ENABLED,
    /**
     * Feature that determines whether the level of includes is limited
     *<p>
     * The default value is 'true', meaning that the level of includes is limited to the depth of 5.
     *<p>
     * The feature is enabled by default.
     */
    LIMIT_INCLUDE_LEVEL,
    /**
     * Feature that determines whether the exceeded level of includes is an error
     *<p>
     * The default value is 'true', meaning that the error is ignored.
     *<p>
     * The feature is disabled by default.
     */
    IGNORE_LIMIT_EXCEEDED_ERROR,
    /**
     * Feature that determines whether the content of include directives is parsed by default.
     *<p>
     * The default value is 'true', meaning that the content is parsed by default.
     *<p>
     * The feature is enabled by default.
     */
    PARSE_BY_DEFAULT;

    private static final Set<IncludeDirectiveFeature> enabled = Set.of(LIMIT_INCLUDE_LEVEL, PARSE_BY_DEFAULT);

    @Override
    public boolean isEnabledByDefault() {
        return enabled.contains(this);
    }
}
