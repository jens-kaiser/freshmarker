package org.freshmarker.core.extension;

import org.freshmarker.api.Extension;
import org.freshmarker.api.FormatterProvider;
import org.freshmarker.api.FunctionProvider;
import org.freshmarker.api.TypeMapperProvider;
import org.freshmarker.api.UserDirectiveProvider;
import org.freshmarker.core.BuiltInVariableProvider;
import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.features.TemplateFeature;
import org.freshmarker.core.features.TemplateFeatures;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.FormatterRegistry;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.plugin.PluginProvider;
import org.freshmarker.core.providers.BeanTemplateObjectProvider;
import org.freshmarker.core.providers.CompoundTemplateObjectProvider;
import org.freshmarker.core.providers.MappingTemplateObjectProvider;
import org.freshmarker.core.providers.RecordTemplateObjectProvider;
import org.freshmarker.core.providers.TemplateObjectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class ExtensionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ExtensionRegistry.class);

    private static final List<Extension> DEFAULT_EXTENSIONS = List.of(new DefaultUserDirectiveProvider(), new DefaultTypeMapperProvider(),
            new DefaultFormatterProvider());

    private final TemplateFeatures templateFeatures = new TemplateFeatures();
    private final Map<BuiltInKey, BuiltIn> builtIns = new HashMap<>();
    private final MappingTemplateObjectProvider mappingTemplateObjectProvider = new MappingTemplateObjectProvider();
    private final List<TemplateObjectProvider> providers = new ArrayList<>();
    private final Map<NameSpaced, UserDirective> userDirectives = new HashMap<>();
    private final Map<String, TemplateFunction> functions = new HashMap<>();
    private FormatterRegistry formatterRegistry = new FormatterRegistry(new HashMap<>());

    private final BuiltInVariableProvider builtInVariableProviders = new BuiltInVariableProvider();

    private final List<Extension> extensions = new ArrayList<>();

    public ExtensionRegistry(TemplateFeature[] enabledFeatures) {
        extensions.addAll(DEFAULT_EXTENSIONS);
        Stream.of(enabledFeatures).forEach(enabledFeature -> templateFeatures.addFeature(enabledFeature, true));
        ServiceLoader.load(PluginProvider.class).forEach(this::registerPlugin);
        ServiceLoader.load(Extension.class).forEach(extensions::add);
    }

    public void registerPlugin(PluginProvider provider) {
        logger.debug("register plugin: {}", provider.getClass().getSimpleName());
        provider.registerFeature(templateFeatures);
        Map<BuiltInKey, BuiltIn> registerBuiltIns = new HashMap<>();
        provider.registerBuildIn(registerBuiltIns, templateFeatures);
        this.builtIns.putAll(registerBuiltIns);
        Map<Class<? extends TemplateObject>, Formatter> registerFormatter = formatterRegistry.formatter();
        provider.registerFormatter(registerFormatter, templateFeatures);
        this.formatterRegistry = new FormatterRegistry(new HashMap<>(registerFormatter));
        Map<Class<?>, Function<Object, TemplateObject>> mapper = new HashMap<>();
        provider.registerMapper(mapper);
        mapper.forEach(mappingTemplateObjectProvider::addMapper);
        List<TemplateObjectProvider> list = new ArrayList<>();
        provider.registerTemplateObjectProvider(list);
        providers.addAll(list);
        Map<String, UserDirective> additionalDirectives = new HashMap<>();
        provider.registerUserDirective(additionalDirectives);
        additionalDirectives.forEach((k, v) -> userDirectives.put(new NameSpaced(null, k), v));
        Map<String, TemplateFunction> additionalFunctions = new HashMap<>();
        provider.registerFunction(additionalFunctions);
        functions.putAll(additionalFunctions);
        Map<String, Function<ProcessContext, TemplateObject>> builtInVariableProviderMap = new HashMap<>();
        provider.registerBuiltInVariableProviders(builtInVariableProviderMap);
        builtInVariableProviders.registerOld(builtInVariableProviderMap);
    }

    public <E extends Extension> Stream<E> stream(Class<E> type) {
        return extensions.stream().filter(type::isInstance).map(type::cast).peek(e -> e.init(templateFeatures));
    }

    public TemplateFeatures getTemplateFeatures() {
        return templateFeatures;
    }

    public Map<BuiltInKey, BuiltIn> getBuiltIns() {
        return builtIns;
    }

    public MappingTemplateObjectProvider getMappingTemplateObjectProvider() {
        return mappingTemplateObjectProvider;
    }

    public List<TemplateObjectProvider> getProviders(ModelSecurityGateway modelSecurityGateway) {
        MappingTemplateObjectProvider newMappingTemplateObjectProvider = mappingTemplateObjectProvider.copy();
        stream(TypeMapperProvider.class).map(TypeMapperProvider::providerTypeMapper).forEach(newMappingTemplateObjectProvider::register);
        BeanTemplateObjectProvider beanTemplateObjectProvider = new BeanTemplateObjectProvider(modelSecurityGateway);
        RecordTemplateObjectProvider recordTemplateObjectProvider = new RecordTemplateObjectProvider(modelSecurityGateway);
        List<TemplateObjectProvider> copy = new ArrayList<>();
        copy.add(newMappingTemplateObjectProvider);
        copy.add(recordTemplateObjectProvider);
        copy.addAll(providers);
        copy.add(new CompoundTemplateObjectProvider());
        copy.add(beanTemplateObjectProvider);
        return copy;
    }

    public Map<NameSpaced, UserDirective> getUserDirectives() {
        Map<NameSpaced, UserDirective> map = new HashMap<>(userDirectives);
        stream(UserDirectiveProvider.class).map(UserDirectiveProvider::provideUserDirectives).map(Map::entrySet)
                .flatMap(Set::stream).forEach(e -> map.put(new NameSpaced(e.getKey()), e.getValue()));
        return map;
    }

    public Map<String, TemplateFunction> getFunctions() {
        Map<String, TemplateFunction> map = new HashMap<>(functions);
        stream(FunctionProvider.class).map(FunctionProvider::provideFunctions).forEach(map::putAll);
        return map;
    }

    public FormatterRegistry getFormatterRegistry() {
        FormatterRegistry copy = new FormatterRegistry(formatterRegistry.formatter());
        stream(FormatterProvider.class).map(FormatterProvider::providerFormatter).forEach(formatterRegistry::registerFormatters);
        return copy;
    }

    public BuiltInVariableProvider getBuiltInVariableProviders() {
        BuiltInVariableProvider copy = builtInVariableProviders.copy();
        stream(org.freshmarker.api.BuiltInVariableProvider.class).map(org.freshmarker.api.BuiltInVariableProvider::provideBuiltInVariables).forEach(copy::register);
        return copy;
    }

    public void register(Extension extension) {
        extensions.add(extension);
    }
}
