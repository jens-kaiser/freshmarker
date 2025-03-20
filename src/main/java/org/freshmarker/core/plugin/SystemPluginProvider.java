package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.directive.CompressDirective;
import org.freshmarker.core.directive.OneLinerDirective;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateLocale;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.primitive.TemplateVersion;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class SystemPluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateVersion> VERSION = new BuiltInKeyBuilder<>(TemplateVersion.class);
    private static final BuiltInKeyBuilder<TemplateString> STRING = new BuiltInKeyBuilder<>(TemplateString.class);
    private static final BuiltInKeyBuilder<TemplateLocale> LOCALE = new BuiltInKeyBuilder<>(TemplateLocale.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(VERSION.of("is_before"), (x, y, e) -> version(x, e).isBefore(parameter(y, e)));
        builtIns.put(VERSION.of("is_after"), (x, y, e) -> version(x, e).isAfter(parameter(y, e)));
        builtIns.put(VERSION.of("is_equal"), (x, y, e) -> version(x, e).isEqual(parameter(y, e)));
        builtIns.put(VERSION.of("major"), (x, y, e) -> version(x, e).major());
        builtIns.put(VERSION.of("minor"), (x, y, e) -> version(x, e).minor());
        builtIns.put(VERSION.of("patch"), (x, y, e) -> version(x, e).patch());
        builtIns.put(STRING.of("version"), (x, y, e) -> new TemplateVersion(x.evaluate(e, TemplateString.class).toString()));
        builtIns.put(LOCALE.of("lang"), (x, y, e) -> locale(x, e).getLanguage());
        builtIns.put(LOCALE.of("language"), (x, y, e) -> locale(x, e).getLanguage());
        builtIns.put(LOCALE.of("language_name"), (x, y, e) -> locale(x, e).getDisplayLanguage(e.getLocale()));
        builtIns.put(LOCALE.of("country"), (x, y, e) -> locale(x, e).getCountry());
        builtIns.put(LOCALE.of("country_name"), (x, y, e) -> locale(x, e).getDisplayCountry(e.getLocale()));
        builtIns.put(STRING.of("locale"), (x, y, e) -> new TemplateLocale(x.evaluate(e, TemplateString.class).toString()));
    }

    @Override
    public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
        mapper.put(Locale.class, o -> new TemplateLocale((Locale) o));
    }

    @Override
    public void registerUserDirective(Map<String, UserDirective> directives) {
        directives.put("oneliner", new OneLinerDirective());
        directives.put("compress", new CompressDirective());
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