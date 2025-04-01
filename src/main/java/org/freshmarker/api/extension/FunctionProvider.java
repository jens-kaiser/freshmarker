package org.freshmarker.api.extension;

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
