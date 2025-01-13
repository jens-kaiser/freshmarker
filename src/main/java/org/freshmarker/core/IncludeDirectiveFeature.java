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
     * Feature that determines whether the content of include directives is parsed.
     *<p>
     * Default value is 'true', meaning that the content is parsed.
     *<p>
     * Feature is enabled by default.
     */
    PARSE
}
