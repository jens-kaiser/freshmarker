package org.freshmarker.api;

import org.freshmarker.core.directive.TemplateFunction;

import java.util.Map;

/**
 * An {@link Extension} to add new functions.
 */
public interface FunctionProvider extends Extension {
    /**
     * Returns a map of functions
     * @return a map of functions
     */
    Map<String, TemplateFunction> provideFunctions();
}
