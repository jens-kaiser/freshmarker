package org.freshmarker.core.plugin;

import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateEnum;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.providers.EnumTemplateObjectProvider;
import org.freshmarker.core.providers.TemplateObjectProvider;

import java.util.List;
import java.util.Map;

public class EnumPluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<? extends TemplateObject> BUILDER = new BuiltInKeyBuilder<>(TemplateEnum.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(BUILDER.of("c"), (x, y, e) -> new TemplateString(((TemplateEnum<?>) x).getValue().name()));
        builtIns.put(BUILDER.of("ordinal"), (x, y, e) -> new TemplateNumber(((TemplateEnum<?>) x).getValue().ordinal()));
    }

    @Override
    public void registerTemplateObjectProvider(List<TemplateObjectProvider> providers) {
        providers.add(new EnumTemplateObjectProvider());
    }
}
