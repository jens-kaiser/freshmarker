package org.freshmarker.api.extension;

import org.freshmarker.core.providers.TemplateObjectProvider;

import java.util.List;

/**
 * An {@link Extension} to add new template object provider.
 */
public interface TemplateObjectProviders extends Extension{
    /**
     * Returns a list of template object provider
     * @return a list of template object provider
     */
    List<TemplateObjectProvider> provideProviders();
}
