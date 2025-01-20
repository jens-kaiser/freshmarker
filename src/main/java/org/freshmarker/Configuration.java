package org.freshmarker;

import ftl.ParseException;
import org.freshmarker.core.BuiltInVariableProvider;
import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.SwitchDirectiveFeature;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.features.TemplateFeature;
import org.freshmarker.core.features.TemplateFeatures;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.FormatterRegistry;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.StandardOutputFormats;
import org.freshmarker.core.IncludeDirectiveFeature;
import org.freshmarker.core.plugin.PluginProvider;
import org.freshmarker.core.providers.BeanTemplateObjectProvider;
import org.freshmarker.core.providers.CompoundTemplateObjectProvider;
import org.freshmarker.core.providers.MappingTemplateObjectProvider;
import org.freshmarker.core.providers.RecordTemplateObjectProvider;
import org.freshmarker.core.providers.TemplateObjectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.function.Function;

public final class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    public interface TemplateBuilder {
        TemplateBuilder withDateTimeFormat(String pattern, ZoneId zoneId);

        TemplateBuilder withDateTimeFormat(String pattern);

        TemplateBuilder withDateFormat(String pattern);

        TemplateBuilder withTimeFormat(String pattern);

        TemplateBuilder withClock(Clock clock);

        TemplateBuilder withLocale(Locale locale);

        TemplateBuilder withZoneId(ZoneId zoneId);

        TemplateBuilder withOutputFormat(String outputFormat);

        TemplateBuilder withOutputFormat(OutputFormat format);

        TemplateBuilder with(TemplateFeature templateFeature);

        TemplateBuilder without(TemplateFeature templateFeature);

        Template getTemplate(Path path) throws ParseException, IOException;

        Template getTemplate(Path path, Charset charset) throws ParseException, IOException;

        Template getTemplate(String name, Reader reader) throws ParseException;

        Template getTemplate(String name, String content) throws ParseException;

        Template getTemplate(Path importPath, String name, Reader reader) throws ParseException;

        Template getTemplate(Path importPath, String name, String content) throws ParseException;
    }

    private Map<BuiltInKey, BuiltIn> builtIns = new HashMap<>();
    private Map<String, OutputFormat> outputs = new HashMap<>();
    private final MappingTemplateObjectProvider mappingTemplateObjectProvider = new MappingTemplateObjectProvider();
    private final ModelSecurityGateway modelSecurityGateway = new ModelSecurityGateway();
    private final List<TemplateObjectProvider> providers;
    private final Map<NameSpaced, UserDirective> userDirectives = new HashMap<>();
    private final Map<String, TemplateFunction> functions = new HashMap<>();
    private FormatterRegistry formatterRegistry = new FormatterRegistry(new HashMap<>());

    private final BuiltInVariableProvider builtInVariableProviders = new BuiltInVariableProvider();

    private TemplateLoader templateLoader;
    private final TemplateFeatures templateFeatures = new TemplateFeatures();

    public Configuration() {
        modelSecurityGateway.addForbiddenPackages("java", "javax", "sun", "com.sun");
        BeanTemplateObjectProvider beanTemplateObjectProvider = new BeanTemplateObjectProvider(modelSecurityGateway);
        RecordTemplateObjectProvider recordTemplateObjectProvider = new RecordTemplateObjectProvider(modelSecurityGateway);
        providers = new ArrayList<>(List.of(mappingTemplateObjectProvider, recordTemplateObjectProvider, new CompoundTemplateObjectProvider(), beanTemplateObjectProvider));

        templateLoader = new DefaultFileSystemTemplateLoader();

        outputs.put("HTML", StandardOutputFormats.HTML);
        outputs.put("XHTML", StandardOutputFormats.HTML);
        outputs.put("XML", StandardOutputFormats.XML);
        outputs.put("plainText", StandardOutputFormats.NONE);
        outputs.put("JavaScript", StandardOutputFormats.JAVASCRIPT);
        outputs.put("JSON", StandardOutputFormats.NONE);
        outputs.put("CSS", StandardOutputFormats.CSS);
        outputs.put("ADOC", StandardOutputFormats.ADOC);

        templateFeatures.addSwitches(IncludeDirectiveFeature.ENABLED, false);
        templateFeatures.addSwitches(IncludeDirectiveFeature.LIMIT_INCLUDE_LEVEL, true);
        templateFeatures.addSwitches(IncludeDirectiveFeature.IGNORE_LIMIT_EXCEEDED_ERROR, false);
        templateFeatures.addSwitches(IncludeDirectiveFeature.PARSE_BY_DEFAULT, true);
        templateFeatures.addSwitches(SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_CASES, false);
        templateFeatures.addSwitches(SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_ONS, false);
        templateFeatures.addSwitches(SwitchDirectiveFeature.ALLOW_ONLY_EQUAL_TYPE_CASES, false);
        templateFeatures.addSwitches(SwitchDirectiveFeature.ALLOW_ONLY_EQUAL_TYPE_ONS, false);

        BuiltInKeyBuilder<TemplateNull> builtInKeyBuilder = new BuiltInKeyBuilder<>(TemplateNull.class);
        builtIns.put(builtInKeyBuilder.of("empty_to_null"), (x, y, e) -> x);
        builtIns.put(builtInKeyBuilder.of("blank_to_null"), (x, y, e) -> x);
        builtIns.put(builtInKeyBuilder.of("trim_to_null"), (x, y, e) -> x);
        registerPlugins();
        registerSimpleMapping(StringBuilder.class, StringBuffer.class, URI.class, URL.class, UUID.class);
    }

    public void registerOutputFormat(String name, OutputFormat format) {
        Map<String, OutputFormat> newOutputs = new HashMap<>(outputs);
        newOutputs.put(Objects.requireNonNull(name), Objects.requireNonNull(format));
        outputs = newOutputs;
    }

    public void registerSimpleMapping(Class<?>... types) {
        for (Class<?> type : types) {
            mappingTemplateObjectProvider.addMapper(type, o -> new TemplateString(o.toString()));
        }
    }

    public void registerSimpleMapping(Class<?> type, Function<Object, String> mapping) {
        Objects.requireNonNull(mapping);
        mappingTemplateObjectProvider.addMapper(type, x -> {
            String apply = mapping.apply(x);
            return apply == null ? TemplateNull.NULL : new TemplateString(apply);
        });
    }

    public void registerUserDirective(String name, UserDirective directive) {
        userDirectives.put(new NameSpaced(null, name), directive);
    }

    public void registerFunction(String name, TemplateFunction function) {
        functions.put(name, function);
    }

    private void registerPlugins() {
        ServiceLoader.load(PluginProvider.class).forEach(this::registerPlugin);
    }

    public void registerPlugin(PluginProvider provider) {
        logger.debug("register plugin: {}", provider.getClass().getSimpleName());
        Map<BuiltInKey, BuiltIn> registerBuiltIns = new HashMap<>(this.builtIns);
        provider.registerBuildIn(registerBuiltIns);
        this.builtIns = registerBuiltIns;
        Map<Class<? extends TemplateObject>, Formatter> registerFormatter = formatterRegistry.formatter();
        provider.registerFormatter(registerFormatter);
        this.formatterRegistry = new FormatterRegistry(new HashMap<>(registerFormatter));
        Map<Class<?>, Function<Object, TemplateObject>> mapper = new HashMap<>();
        provider.registerMapper(mapper);
        mapper.forEach(mappingTemplateObjectProvider::addMapper);
        List<TemplateObjectProvider> list = new ArrayList<>();
        provider.registerTemplateObjectProvider(list);
        providers.addAll(providers.size() - 2, list);
        Map<String, UserDirective> additionalDirectives = new HashMap<>();
        provider.registerUserDirective(additionalDirectives);
        additionalDirectives.forEach((k, v) -> userDirectives.put(new NameSpaced(null, k), v));
        Map<String, TemplateFunction> additionalFunctions = new HashMap<>();
        provider.registerFunction(additionalFunctions);
        functions.putAll(additionalFunctions);
        Map<String, Function<ProcessContext, TemplateObject>> builtInVariableProviderMap = new HashMap<>();
        provider.registerBuiltInVariableProviders(builtInVariableProviderMap);
        builtInVariableProviders.register(builtInVariableProviderMap);
    }

    /**
     * Creates a new {@link TemplateBuilder} based on the current {@code Configuration}.
     *
     * @return a new {@code TemplateBuilder}
     */
    public TemplateBuilder builder() {
        return new DefaultTemplateBuilder(this, getContext(), Locale.getDefault(), ZoneId.systemDefault(), StandardOutputFormats.NONE, Clock.systemUTC(), templateFeatures.create());
    }

    public void setTemplateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public ModelSecurityGateway getSecurity() {
        return modelSecurityGateway;
    }

    public StaticContext getContext() {
        return new StaticContext(builtIns, formatterRegistry.formatter(), outputs, providers, userDirectives, templateLoader, functions);
    }

    public void registerFormatter(Class<? extends TemplateObject> type, Formatter formatter) {
        formatterRegistry.registerFormatter(type, formatter);
    }

    public void registerNumberFormatter(String pattern) {
        formatterRegistry.registerFormatter("number", pattern);
    }

    public void registerFormatter(String type, String pattern) {
        formatterRegistry.registerFormatter(type, pattern);
    }

    public BuiltInVariableProvider getBuiltInVariableProviders() {
        return builtInVariableProviders;
    }
}

