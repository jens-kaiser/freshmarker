package org.freshmarker.api;

import org.freshmarker.core.providers.TemplateObjectProvider;

import java.util.List;

public interface TemplateObjectProviders extends Extension{
    List<TemplateObjectProvider> provideProviders();
}
