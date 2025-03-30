package org.freshmarker;

import org.freshmarker.api.Extension;
import org.freshmarker.api.FormatterProvider;
import org.freshmarker.api.FunctionProvider;
import org.freshmarker.api.UserDirectiveProvider;
import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.extension.ExtensionRegistry;
import org.freshmarker.core.features.TemplateFeature;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.NumberFormatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateInstant;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplateZonedDateTime;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.StandardOutputFormats;
import org.freshmarker.core.plugin.PluginProvider;

import java.time.Clock;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class Configuration {
    private final Map<String, OutputFormat> outputs;
    private final ModelSecurityGateway modelSecurityGateway = new ModelSecurityGateway();

    private TemplateLoader templateLoader;

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
        extensionRegistry.register((UserDirectiveProvider) () -> Map.of(name, directive));
    }

    public void registerFunction(String name, TemplateFunction function) {
        extensionRegistry.register((FunctionProvider) () -> Map.of(name, function));
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
        return new DefaultTemplateBuilder(context, Locale.getDefault(), ZoneId.systemDefault(), StandardOutputFormats.NONE, Clock.systemUTC(), extensionRegistry.getTemplateFeatures().create());
    }

    public void setTemplateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public ModelSecurityGateway getSecurity() {
        return modelSecurityGateway;
    }

    public void registerFormatter(Class<? extends TemplateObject> type, Formatter formatter) {
        extensionRegistry.register((FormatterProvider) () -> Map.of(type, formatter));
    }

    public void registerNumberFormatter(String pattern) {
        extensionRegistry.register((FormatterProvider) () -> Map.of(TemplateNumber.class, new NumberFormatter(pattern)));
    }

    public void registerFormatter(String type, String pattern) {
        extensionRegistry.register((FormatterProvider) () ->
        switch (type) {
            case "number" -> Map.of(TemplateNumber.class, new NumberFormatter(pattern));
            case "zoned-date-time" ->  Map.of(
                    TemplateZonedDateTime.class, new DateTimeFormatter(pattern),
                    TemplateInstant.class, new DateTimeFormatter(pattern, ZoneId.systemDefault()));
            case "date-time" ->  Map.of(TemplateLocalDateTime.class, new DateTimeFormatter(pattern));
            case "date" ->  Map.of(TemplateLocalDate.class, new DateFormatter(pattern));
            case "time" ->  Map.of(TemplateLocalTime.class, new TimeFormatter(pattern, ZoneId.systemDefault()));
            default -> throw new IllegalStateException("Unexpected value: " + type);
        });
    }
}
