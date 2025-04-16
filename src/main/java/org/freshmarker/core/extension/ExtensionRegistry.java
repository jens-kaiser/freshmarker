package org.freshmarker.core.extension;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.FeatureSet;
import org.freshmarker.api.Formatter;
import org.freshmarker.api.OutputFormat;
import org.freshmarker.api.TemplateFunction;
import org.freshmarker.api.UserDirective;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.Extension;
import org.freshmarker.api.extension.FormatterProvider;
import org.freshmarker.api.extension.FunctionProvider;
import org.freshmarker.api.extension.OutputFormatProvider;
import org.freshmarker.api.TemplateFeature;
import org.freshmarker.api.extension.TemplateFeatureProvider;
import org.freshmarker.api.extension.TemplateObjectProviders;
import org.freshmarker.api.extension.TypeMapperProvider;
import org.freshmarker.api.extension.UserDirectiveProvider;
import org.freshmarker.core.BuiltInVariableProvider;
import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.features.TemplateFeatures;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.plugin.PluginProvider;
import org.freshmarker.core.providers.BeanTemplateObjectProvider;
import org.freshmarker.core.providers.CompoundTemplateObjectProvider;
import org.freshmarker.core.providers.EnumTemplateObjectProvider;
import org.freshmarker.core.providers.MappingTemplateObjectProvider;
import org.freshmarker.core.providers.RecordTemplateObjectProvider;
import org.freshmarker.core.providers.TemplateObjectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;

public class ExtensionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ExtensionRegistry.class);

    private static final List<Extension> DEFAULT_EXTENSIONS = List.of(new DefaultFeatureProvider(), new DefaultUserDirectiveProvider(), new DefaultTypeMapperProvider(),
            new DefaultFormatterProvider(), new DefaultOutputFormatProvider());

    private final TemplateFeatures templateFeatures;

    private final PluginProviderRegistry pluginProviderRegistry;

    private final List<Extension> extensions;

    private final FeatureSet featureSet;

    public ExtensionRegistry(TemplateFeature[] enabledFeatures) {
        templateFeatures = new TemplateFeatures();
        featureSet = templateFeatures;
        pluginProviderRegistry = new PluginProviderRegistry();
        extensions = new ArrayList<>(DEFAULT_EXTENSIONS);
        Stream.of(enabledFeatures).forEach(enabledFeature -> templateFeatures.addFeature(enabledFeature, true));
        ServiceLoader.load(PluginProvider.class).forEach(this::registerPlugin);
        ServiceLoader.load(Extension.class).forEach(extensions::add);
        stream(TemplateFeatureProvider.class).map(TemplateFeatureProvider::provideFeatures).flatMap(Set::stream).forEach(templateFeatures::addFeatures);
    }

    public ExtensionRegistry(ExtensionRegistry registry, FeatureSet featureSet) {
        templateFeatures = registry.templateFeatures;
        this.featureSet = featureSet;
        pluginProviderRegistry = new PluginProviderRegistry(registry.pluginProviderRegistry);
        extensions = List.copyOf(registry.extensions);
    }

    public void registerPlugin(PluginProvider provider) {
        pluginProviderRegistry.registerPlugin(provider, templateFeatures);
    }

    public void register(Extension extension) {
        logger.debug("registering: {}", extension.getClass());
        extensions.add(extension);
        if (extension instanceof TemplateFeatureProvider provider) {
            provider.provideFeatures().forEach(templateFeatures::addFeatures);
        }
    }

    public <E extends Extension> Stream<E> stream(Class<E> type) {
        return extensions.stream().filter(type::isInstance).map(type::cast).peek(e -> e.init(featureSet));
    }

    public TemplateFeatures getTemplateFeatures() {
        return templateFeatures;
    }

    public Map<BuiltInKey, BuiltIn> getBuiltIns() {
        Map<org.freshmarker.core.buildin.BuiltInKey, BuiltIn> map = new HashMap<>(pluginProviderRegistry.getBuiltIns());
        stream(BuiltInProvider.class).map(BuiltInProvider::provideBuiltInRegister).filter(Objects::nonNull).forEach(r -> {
            for (Entry<Class<? extends TemplateObject>, Map<String, BuiltIn>> entry : r.asMap().entrySet()) {
                for (Entry<String, BuiltIn> builtInEntry : entry.getValue().entrySet()) {
                    map.put(new BuiltInKey(entry.getKey(), builtInEntry.getKey()), builtInEntry.getValue());
                }
            }
        });
        return map;
    }

    public MappingTemplateObjectProvider getMappingTemplateObjectProvider() {
        return pluginProviderRegistry.getMappingTemplateObjectProvider();
    }

    public List<TemplateObjectProvider> getProviders(ModelSecurityGateway modelSecurityGateway) {
        List<TemplateObjectProvider> templateObjectProviders = new ArrayList<>(pluginProviderRegistry.getProviders());
        stream(TemplateObjectProviders.class).map(TemplateObjectProviders::provideProviders).forEach(templateObjectProviders::addAll);
        MappingTemplateObjectProvider newMappingTemplateObjectProvider = pluginProviderRegistry.getMappingTemplateObjectProvider().copy();
        stream(TypeMapperProvider.class).map(TypeMapperProvider::providerTypeMapper).forEach(newMappingTemplateObjectProvider::register);
        List<TemplateObjectProvider> copy = new ArrayList<>();
        copy.add(newMappingTemplateObjectProvider);
        copy.add(new RecordTemplateObjectProvider(modelSecurityGateway));
        copy.add(new EnumTemplateObjectProvider());
        copy.addAll(templateObjectProviders);
        copy.add(new CompoundTemplateObjectProvider());
        copy.add(new BeanTemplateObjectProvider(modelSecurityGateway));
        return copy;
    }

    public Map<NameSpaced, UserDirective> getUserDirectives() {
        Map<NameSpaced, UserDirective> map = new HashMap<>(pluginProviderRegistry.getUserDirectives());
        stream(UserDirectiveProvider.class).map(UserDirectiveProvider::provideUserDirectives).map(Map::entrySet)
                .flatMap(Set::stream).forEach(e -> map.put(new NameSpaced(e.getKey()), e.getValue()));
        return map;
    }

    public Map<String, TemplateFunction> getFunctions() {
        Map<String, TemplateFunction> map = new HashMap<>(pluginProviderRegistry.getFunctions());
        stream(FunctionProvider.class).map(FunctionProvider::provideFunctions).forEach(map::putAll);
        return map;
    }

    public Map<Class<? extends TemplateObject>, Formatter> getFormatterRegistry() {
        Map<Class<? extends TemplateObject>, Formatter> formatter = new HashMap<>(pluginProviderRegistry.getFormatterRegistry());
        stream(FormatterProvider.class).map(FormatterProvider::providerFormatter).forEach(formatter::putAll);
        return formatter;
    }

    public BuiltInVariableProvider getBuiltInVariableProviders() {
        BuiltInVariableProvider copy = pluginProviderRegistry.getBuiltInVariableProviders().copy();
        stream(org.freshmarker.api.extension.BuiltInVariableProvider.class).map(org.freshmarker.api.extension.BuiltInVariableProvider::provideBuiltInVariables).forEach(copy::register);
        return copy;
    }

    public Map<String, OutputFormat> getOutputFormats() {
        Map<String, OutputFormat> result = new HashMap<>();
        stream(OutputFormatProvider.class).map(OutputFormatProvider::provideOutputFormats).forEach(result::putAll);
        return result;
    }
}
