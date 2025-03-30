package org.freshmarker.core.plugin;

import org.freshmarker.core.providers.EnumTemplateObjectProvider;
import org.freshmarker.core.providers.TemplateObjectProvider;

import java.util.List;

public final class EnumPluginProvider implements PluginProvider {

    @Override
    public void registerTemplateObjectProvider(List<TemplateObjectProvider> providers) {
        providers.add(new EnumTemplateObjectProvider());
    }
}
