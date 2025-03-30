package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.extension.TypedBuiltInProvider;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public final class BooleanPluginProvider extends TypedBuiltInProvider<TemplateBoolean> {

    public BooleanPluginProvider() {
        super(TemplateBoolean.class);
    }

    @Override
    public Map<BuiltInKey, BuiltIn> provideBuiltIns() {
        return Map.ofEntries(
                entry("c", BuiltIn.string()),
                entry("then", BooleanPluginProvider::thenBuildIn),
                entry("string", BooleanPluginProvider::stringBuiltIn),
                entry("h", BooleanPluginProvider::humanBuiltIn));
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
