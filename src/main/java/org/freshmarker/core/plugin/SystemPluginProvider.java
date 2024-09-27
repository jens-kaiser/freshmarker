package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateLocale;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.primitive.TemplateVersion;

import java.util.List;
import java.util.Map;

public class SystemPluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateVersion> VERSION = new BuiltInKeyBuilder<>(TemplateVersion.class);
    private static final BuiltInKeyBuilder<TemplateString> STRING = new BuiltInKeyBuilder<>(TemplateString.class);
    private static final BuiltInKeyBuilder<TemplateLocale> LOCALE = new BuiltInKeyBuilder<>(TemplateLocale.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(VERSION.of("is_before"), (x11, y11, e11) -> version(x11, e11).isBefore(parameter(y11, e11)));
        builtIns.put(VERSION.of("is_after"), (x10, y10, e10) -> version(x10, e10).isAfter(parameter(y10, e10)));
        builtIns.put(VERSION.of("is_equal"), (x9, y9, e9) -> version(x9, e9).isEqual(parameter(y9, e9)));
        builtIns.put(VERSION.of("major"), (x8, y8, e8) -> version(x8, e8).major());
        builtIns.put(VERSION.of("minor"), (x7, y7, e7) -> version(x7, e7).minor());
        builtIns.put(VERSION.of("patch"), (x6, y6, e6) -> version(x6, e6).patch());
        builtIns.put(STRING.of("version"), (x5, y5, e5) -> new TemplateVersion(x5.evaluate(e5, TemplateString.class).toString()));
        builtIns.put(LOCALE.of("lang"), (x4, y4, e4) -> locale(x4, e4).getLanguage());
        builtIns.put(LOCALE.of("language"), (x3, y3, e3) -> locale(x3, e3).getLanguage());
        builtIns.put(LOCALE.of("country"), (x2, y2, e2) -> locale(x2, e2).getCountry());
        builtIns.put(STRING.of("locale"), (x1, y1, e1) -> new TemplateLocale(x1.evaluate(e1, TemplateString.class).toString()));
    }

    private static TemplateVersion version(TemplateObject x, ProcessContext e) {
        return x.evaluate(e, TemplateVersion.class);
    }

    private static TemplateLocale locale(TemplateObject x, ProcessContext e) {
        return x.evaluate(e, TemplateLocale.class);
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
}