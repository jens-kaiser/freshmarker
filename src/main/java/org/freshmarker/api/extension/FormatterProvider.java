package org.freshmarker.api.extension;

import org.freshmarker.core.model.TemplateObject;

import java.util.Map;

/**
 * An {@link Extension} to add new formatter.
 */
public interface FormatterProvider extends Extension {
    /**
     * Returns a map of formatter
     * @return a map of formatter
     */
    Map<Class<? extends TemplateObject>, Formatter> providerFormatter();
}
