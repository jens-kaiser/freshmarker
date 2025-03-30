package org.freshmarker.core;

import org.freshmarker.TemplateLoader;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.extension.ExtensionRegistry;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.providers.TemplateObjectProvider;

import java.util.List;
import java.util.Map;

public record StaticContext(Map<BuiltInKey, BuiltIn> builtIns, Map<Class<? extends TemplateObject>, Formatter> formatter, Map<String, OutputFormat> outputs,
                            List<TemplateObjectProvider> providers, Map<NameSpaced, UserDirective> userDirectives,
                            TemplateLoader templateLoader, Map<String, TemplateFunction> functions, BuiltInVariableProvider builtInVariableProviders) {
    public StaticContext(ExtensionRegistry registry, Map<String, OutputFormat> outputs, ModelSecurityGateway modelSecurityGateway, TemplateLoader templateLoader) {
        this(Map.copyOf(registry.getBuiltIns()), registry.getFormatterRegistry(), Map.copyOf(outputs), List.copyOf(registry.getProviders(modelSecurityGateway)),
                Map.copyOf(registry.getUserDirectives()), templateLoader, Map.copyOf(registry.getFunctions()), registry.getBuiltInVariableProviders().copy());
    }
}
