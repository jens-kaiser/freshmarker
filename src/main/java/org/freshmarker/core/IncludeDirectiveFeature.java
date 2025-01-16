package org.freshmarker.core;

import org.freshmarker.core.features.TemplateFeature;

public enum IncludeDirectiveFeature implements TemplateFeature {
    /**
     * Feature that determines whether include directives are evaluated.
     *<p>
     * Default value is 'false', meaning that include directives are ignored.
     *<p>
     * Feature is disabled by default.
     */
    ENABLED,
    /**
     * Feature that determines whether the level of includes is limited
     *<p>
     * Default value is 'true', meaning that the level of includes is limited to the depth of 5.
     *<p>
     * Feature is enabled by default.
     */
    LIMIT_INCLUDE_LEVEL,
    /**
     * Feature that determines whether the exceeded level of includes is an error
     *<p>
     * Default value is 'true', meaning that the error is ignored.
     *<p>
     * Feature is disabled by default.
     */
    IGNORE_LIMIT_EXCEEDED_ERROR,
    /**
     * Feature that determines whether the content of include directives is parsed.
     *<p>
     * Default value is 'true', meaning that the content is parsed.
     *<p>
     * Feature is enabled by default.
     */
    PARSE
}
