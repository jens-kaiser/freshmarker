package org.freshmarker.core;

import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateLocale;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.primitive.TemplateVersion;
import org.freshmarker.core.model.temporal.TemplateZonedDateTime;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BuiltInVariableProvider {
    private final Map<String, Function<ProcessContext, TemplateObject>> providers = new HashMap<>();

    public TemplateObject provide(String name, ProcessContext context) {
        return switch (name) {
            case "now" -> new TemplateZonedDateTime(ZonedDateTime.now(context.getClock()));
            case "locale" -> new TemplateLocale(context.getLocale());
            case "country" -> new TemplateString(context.getLocale().getCountry());
            case "lang", "language" -> new TemplateString(context.getLocale().getLanguage());
            case "version" -> new TemplateVersion(getClass().getPackage().getImplementationVersion());
            default -> handleRegisteredProviders(name, context);
        };
    }

    private TemplateObject handleRegisteredProviders(String name, ProcessContext context) {
        Function<ProcessContext, TemplateObject> provider = providers.get(name);
        if (provider == null) {
            throw new IllegalStateException("Unexpected built-in variable: " + name);
        }
        return provider.apply(context);
    }

    public void register(Map<String, BuiltInVariable> providers) {
        this.providers.putAll(providers);
    }

    public void registerOld(Map<String, Function<ProcessContext, TemplateObject>> providers) {
        this.providers.putAll(providers);
    }

    public BuiltInVariableProvider copy() {
        BuiltInVariableProvider provider = new BuiltInVariableProvider();
        provider.providers.putAll(providers);
        return provider;
    }
}