package org.freshmarker.core;

import org.freshmarker.api.TemplateFeature;

public enum ReductionFeature implements TemplateFeature {
    /**
     * Feature that determines whether list directives are unrolled.
     * Only list directives that do not exceed a configurable number of elements are unrolled.
     * The default value is 5.
     *<p>
     * The default value is 'false', meaning that list directives are not unfolded.
     *<p>
     * The feature is disabled by default.
     */
    UNROLL_LIST,

    /**
     * Feature that determines whether constant fragments are merged.
     *<p>
     * The default value is 'false', meaning that constant fragments are not merged.
     *<p>
     * The feature is disabled by default.
     */
    MERGE_CONSTANT_FRAGMENTS;

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
