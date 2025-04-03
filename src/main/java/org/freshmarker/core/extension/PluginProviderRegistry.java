package org.freshmarker.core.extension;

import org.freshmarker.core.BuiltInVariableProvider;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.features.TemplateFeatures;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.plugin.PluginProvider;
import org.freshmarker.core.providers.MappingTemplateObjectProvider;
import org.freshmarker.core.providers.TemplateObjectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PluginProviderRegistry {
    private static final Logger logger = LoggerFactory.getLogger(PluginProviderRegistry.class);

    private final Map<org.freshmarker.core.buildin.BuiltInKey, org.freshmarker.api.extension.BuiltIn> builtIns = new HashMap<>();
    private final MappingTemplateObjectProvider mappingTemplateObjectProvider = new MappingTemplateObjectProvider();
    private final List<TemplateObjectProvider> providers = new ArrayList<>();
    private final Map<NameSpaced, org.freshmarker.api.UserDirective> userDirectives = new HashMap<>();
    private final Map<String, org.freshmarker.api.TemplateFunction> functions = new HashMap<>();
    private final Map<Class<? extends TemplateObject>, org.freshmarker.api.Formatter> formatterRegistry = new HashMap<>();

    private final BuiltInVariableProvider builtInVariableProviders = new BuiltInVariableProvider();

    public void registerPlugin(PluginProvider provider, TemplateFeatures templateFeatures) {
        logger.debug("register plugin: {}", provider.getClass().getSimpleName());
        provider.registerFeature(templateFeatures);
        Map<BuiltInKey, BuiltIn> registerBuiltIns = new HashMap<>();
        provider.registerBuildIn(registerBuiltIns, templateFeatures);
        this.builtIns.putAll(registerBuiltIns);
        Map<Class<? extends TemplateObject>, Formatter> registerFormatter = new HashMap<>();
        provider.registerFormatter(registerFormatter, templateFeatures);
        this.formatterRegistry.putAll(registerFormatter);
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

    public Map<BuiltInKey, org.freshmarker.api.extension.BuiltIn> getBuiltIns() {
        return builtIns;
    }

    public MappingTemplateObjectProvider getMappingTemplateObjectProvider() {
        return mappingTemplateObjectProvider;
    }

    public List<TemplateObjectProvider> getProviders() {
        return providers;
    }

    public Map<NameSpaced, org.freshmarker.api.UserDirective> getUserDirectives() {
        return userDirectives;
    }

    public Map<String, org.freshmarker.api.TemplateFunction> getFunctions() {
        return functions;
    }

    public Map<Class<? extends TemplateObject>, org.freshmarker.api.Formatter> getFormatterRegistry() {
        return formatterRegistry;
    }

    public BuiltInVariableProvider getBuiltInVariableProviders() {
        return builtInVariableProviders;
    }
}
