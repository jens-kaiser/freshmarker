package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.formatter.BooleanFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

public final class BooleanPluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateBoolean> BUILDER = new BuiltInKeyBuilder<>(TemplateBoolean.class);

    @Override
    public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
        mapper.put(Boolean.class, o -> Boolean.TRUE.equals(o) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE);
    }

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(BUILDER.of("then"), BooleanPluginProvider::thenBuildIn);
        builtIns.put(BUILDER.of("string"), BooleanPluginProvider::stringBuiltIn);
        builtIns.put(BUILDER.of("h"), BooleanPluginProvider::humanBuiltIn);
    }

    @Override
    public void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {
        formatter.put(TemplateBoolean.class, new BooleanFormatter("yes", "no"));
    }

    private static TemplateObject thenBuildIn(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        BuiltInHelper.checkParametersLength(parameters, 2);
        return value == TemplateBoolean.TRUE ? parameters.getFirst().evaluateToObject(context) : parameters.get(1).evaluateToObject(context);
    }

    private static TemplateString stringBuiltIn(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        BuiltInHelper.checkParametersLength(parameters, 2);
        return value == TemplateBoolean.TRUE ? parameters.getFirst().evaluate(context, TemplateString.class) : parameters.get(1).evaluate(context, TemplateString.class);
    }

    private static TemplateString humanBuiltIn(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        return new TemplateString(ResourceBundle.getBundle("freshmarker", context.getLocale()).getString("boolean." + value));
    }
}
