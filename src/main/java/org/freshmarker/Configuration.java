package org.freshmarker;

import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.BuiltinHandlingFeature;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.SwitchDirectiveFeature;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.extension.ExtensionRegistry;
import org.freshmarker.core.features.TemplateFeature;
import org.freshmarker.core.features.TemplateFeatures;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.StandardOutputFormats;
import org.freshmarker.core.IncludeDirectiveFeature;
import org.freshmarker.core.plugin.PluginProvider;

import java.net.URI;
import java.net.URL;
import java.time.Clock;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public final class Configuration {
    private final Map<String, OutputFormat> outputs;
    private final ModelSecurityGateway modelSecurityGateway = new ModelSecurityGateway();

    private TemplateLoader templateLoader;
    private final TemplateFeatures templateFeatures;

    private final ExtensionRegistry extensionRegistry;

    public Configuration(TemplateFeature... enabledFeatures) {
        modelSecurityGateway.addForbiddenPackages("java", "javax", "sun", "com.sun");
        templateLoader = new DefaultFileSystemTemplateLoader();
        extensionRegistry = new ExtensionRegistry(enabledFeatures);

        outputs = new HashMap<>();
        outputs.put("HTML", StandardOutputFormats.HTML);
        outputs.put("XHTML", StandardOutputFormats.HTML);
        outputs.put("XML", StandardOutputFormats.XML);
        outputs.put("plainText", StandardOutputFormats.NONE);
        outputs.put("JavaScript", StandardOutputFormats.JAVASCRIPT);
        outputs.put("JSON", StandardOutputFormats.NONE);
        outputs.put("CSS", StandardOutputFormats.CSS);
        outputs.put("ADOC", StandardOutputFormats.ADOC);

        templateFeatures = extensionRegistry.getTemplateFeatures();
        templateFeatures.addFeatures(IncludeDirectiveFeature.values());
        templateFeatures.addFeatures(SwitchDirectiveFeature.values());
        templateFeatures.addFeatures(BuiltinHandlingFeature.values());

        registerSimpleMapping(StringBuilder.class, StringBuffer.class, URI.class, URL.class, UUID.class);
    }

    public void registerOutputFormat(String name, OutputFormat format) {
        outputs.put(Objects.requireNonNull(name), Objects.requireNonNull(format));
    }

    public void registerSimpleMapping(Class<?>... types) {
        for (Class<?> type : types) {
            extensionRegistry.getMappingTemplateObjectProvider().addMapper(type, o -> new TemplateString(o.toString()));
        }
    }

    public void registerSimpleMapping(Class<?> type, Function<Object, String> mapping) {
        Objects.requireNonNull(mapping);
        extensionRegistry.getMappingTemplateObjectProvider().addMapper(type, x -> {
            String apply = mapping.apply(x);
            return apply == null ? TemplateNull.NULL : new TemplateString(apply);
        });
    }

    public void registerUserDirective(String name, UserDirective directive) {
        extensionRegistry.register(new UserDirectiveAdapter(name, directive));
    }

    public void registerFunction(String name, TemplateFunction function) {
        extensionRegistry.register(new FunctionAdapter(name, function));
    }

    public void registerPlugin(PluginProvider provider) {
        extensionRegistry.registerPlugin(provider);
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
        StaticContext context = new StaticContext(extensionRegistry, Map.copyOf(outputs),modelSecurityGateway, templateLoader);
        return new DefaultTemplateBuilder(context, Locale.getDefault(), ZoneId.systemDefault(), StandardOutputFormats.NONE, Clock.systemUTC(), templateFeatures.create());
    }

    public void setTemplateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public ModelSecurityGateway getSecurity() {
        return modelSecurityGateway;
    }

    public void registerFormatter(Class<? extends TemplateObject> type, Formatter formatter) {
        extensionRegistry.getFormatterRegistry().registerFormatter(type, formatter);
    }

    public void registerNumberFormatter(String pattern) {
        extensionRegistry.getFormatterRegistry().registerFormatter("number", pattern);
    }

    public void registerFormatter(String type, String pattern) {
        extensionRegistry.getFormatterRegistry().registerFormatter(type, pattern);
    }
}
