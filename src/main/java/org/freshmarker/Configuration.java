package org.freshmarker;

import org.freshmarker.api.OutputFormat;
import org.freshmarker.api.TemplateLoader;
import org.freshmarker.api.extension.Extension;
import org.freshmarker.api.Formatter;
import org.freshmarker.api.extension.FormatterProvider;
import org.freshmarker.api.extension.FunctionProvider;
import org.freshmarker.api.extension.OutputFormatProvider;
import org.freshmarker.api.TemplateFeature;
import org.freshmarker.api.TemplateFunction;
import org.freshmarker.api.extension.TypeMapperProvider;
import org.freshmarker.api.UserDirective;
import org.freshmarker.api.extension.UserDirectiveProvider;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.extension.ExtensionRegistry;
import org.freshmarker.core.extension.PatternBasedFormatterProvider;
import org.freshmarker.core.formatter.NumberFormatter;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class Configuration {
    private org.freshmarker.api.TemplateLoader templateLoader;

    private final ExtensionRegistry extensionRegistry;

    public Configuration(TemplateFeature... enabledFeatures) {
        templateLoader = new DefaultFileSystemTemplateLoader();
        extensionRegistry = new ExtensionRegistry(enabledFeatures);
    }

    public void registerOutputFormat(String name, OutputFormat format) {
        extensionRegistry.register((OutputFormatProvider) () -> Map.of(name, format));
    }

    public void registerSimpleMapping(Class<?>... types) {
        for (Class<?> type : types) {
            extensionRegistry.register((TypeMapperProvider) () -> Map.of(type, o -> new TemplateString(o.toString())));
        }
    }

    public void registerSimpleMapping(Class<?> type, Function<Object, String> mapping) {
        Objects.requireNonNull(mapping);
        extensionRegistry.register((TypeMapperProvider) () -> Map.of(type, x -> {
            String apply = mapping.apply(x);
            return apply == null ? TemplateNull.NULL : new TemplateString(apply);
        }));
    }

    public void registerUserDirective(String name, UserDirective directive) {
        extensionRegistry.register((UserDirectiveProvider) () -> Map.of(name, directive));
    }

    public void registerFunction(String name, TemplateFunction function) {
        extensionRegistry.register((FunctionProvider) () -> Map.of(name, function));
    }

    public void register(Extension extension) {
        extensionRegistry.register(extension);
    }

    /**
     * Creates a new {@link TemplateBuilder} based on the current {@code Configuration}.
     *
     * @return a new {@code TemplateBuilder}
     */
    public TemplateBuilder builder() {
        ExtensionRegistry copy = new ExtensionRegistry(extensionRegistry, extensionRegistry.getTemplateFeatures().create());
        StaticContext context = new StaticContext(copy, copy.getFormatterRegistry());
        return new DefaultTemplateBuilder(context, copy.getTemplateFeatures().create(), templateLoader);
    }

    public void setTemplateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public void registerFormatter(Class<? extends TemplateObject> type, Formatter formatter) {
        extensionRegistry.register((FormatterProvider) () -> Map.of(type, formatter));
    }

    public void registerNumberFormatter(String pattern) {
        extensionRegistry.register((FormatterProvider) () -> Map.of(TemplateNumber.class, new NumberFormatter(pattern)));
    }

    public void registerFormatter(String type, String pattern) {
        extensionRegistry.register(new PatternBasedFormatterProvider(type, pattern));
    }
}
