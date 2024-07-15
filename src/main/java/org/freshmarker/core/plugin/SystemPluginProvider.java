package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInFunction;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.FunctionalBuiltIn;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.primitive.TemplateVersion;

import java.util.List;
import java.util.Map;

public class SystemPluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateVersion> VERSION = new BuiltInKeyBuilder<>(TemplateVersion.class);
    private static final BuiltInKeyBuilder<TemplateString> STRING = new BuiltInKeyBuilder<>(TemplateString.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        register(builtIns, VERSION.of("is_before"), (x, y, e) -> version(x, e).isBefore(parameter(y, e)));
        register(builtIns, VERSION.of("is_after"), (x, y, e) -> version(x, e).isAfter(parameter(y, e)));
        register(builtIns, VERSION.of("is_equal"), (x, y, e) -> version(x, e).isEqual(parameter(y, e)));
        register(builtIns, VERSION.of("major"), (x, y, e) -> version(x, e).major());
        register(builtIns, VERSION.of("minor"), (x, y, e) -> version(x, e).minor());
        register(builtIns, VERSION.of("patch"), (x, y, e) -> version(x, e).patch());
        register(builtIns, STRING.of("version"), (x, y, e) -> x.evaluateToObject(e).asString().map(TemplatePrimitive::toString)
                .map(TemplateVersion::new).orElseThrow());
    }

    private static TemplateVersion version(TemplateObject x, ProcessContext e) {
        return x.evaluate(e, TemplateVersion.class);
    }

    private static TemplateVersion parameter(List<TemplateObject> objects, ProcessContext context) {
        if (objects.size() != 1) {
            throw new IllegalArgumentException("invalid argument lists: size=" + objects.size());
        }
        TemplateObject value = objects.getFirst().evaluateToObject(context);
        return switch (value) {
            case TemplateVersion version -> version;
            case TemplateString string -> new TemplateVersion(string.toString());
            default -> throw new IllegalStateException("invalid type: " + value.getModelType());
        };
    }

    private void register(Map<BuiltInKey, BuiltIn> buildIns, BuiltInKey builtInKey, BuiltInFunction function) {
        buildIns.put(builtInKey, new FunctionalBuiltIn(function));
    }
}