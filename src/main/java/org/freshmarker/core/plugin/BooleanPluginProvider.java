package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.List;
import java.util.Map;

public class BooleanPluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateBoolean> BUILDER = new BuiltInKeyBuilder<>(TemplateBoolean.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(BUILDER.of("then"), BooleanPluginProvider::thenBuildIn);
        builtIns.put(BUILDER.of("string"), BooleanPluginProvider::stringBuiltIn);
    }

    private static TemplateObject thenBuildIn(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        BuiltInHelper.checkParametersLength(parameters, 2);
        return value == TemplateBoolean.TRUE ? parameters.getFirst().evaluateToObject(context) : parameters.get(1).evaluateToObject(context);
    }

    public static TemplateString stringBuiltIn(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        BuiltInHelper.checkParametersLength(parameters, 2);
        return value == TemplateBoolean.TRUE ? (TemplateString) parameters.getFirst() : (TemplateString) parameters.get(1);
    }
}
