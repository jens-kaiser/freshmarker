package org.freshmarker.core;

import org.freshmarker.api.TemplateLoader;
import org.freshmarker.api.Formatter;
import org.freshmarker.api.TemplateFunction;
import org.freshmarker.api.UserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.extension.BuiltInRepository;
import org.freshmarker.core.extension.ExtensionRegistry;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.api.OutputFormat;
import org.freshmarker.core.providers.TemplateObjectProvider;

import java.util.List;
import java.util.Map;

public record StaticContext(ExtensionRegistry registry, BuiltInRepository builtIns, Map<Class<? extends TemplateObject>, Formatter> formatter, Map<String, OutputFormat> outputs,
                            List<TemplateObjectProvider> providers, Map<NameSpaced, UserDirective> userDirectives,
                            TemplateLoader templateLoader, Map<String, TemplateFunction> functions, BuiltInVariableProvider builtInVariableProviders) {
    public StaticContext(ExtensionRegistry registry, TemplateLoader templateLoader) {
        this(registry, registry.getBuiltIns(), registry.getFormatterRegistry(), registry.getOutputFormats(), registry.getProviders(),
                registry.getUserDirectives(), templateLoader, registry.getFunctions(), registry.getBuiltInVariableProviders());
    }

    public StaticContext(ExtensionRegistry registry, TemplateLoader templateLoader, Map<Class<? extends TemplateObject>, Formatter> combinedFormatters) {
        this(registry, registry.getBuiltIns(), combinedFormatters, registry.getOutputFormats(), registry.getProviders(),
                registry.getUserDirectives(), templateLoader, registry.getFunctions(), registry.getBuiltInVariableProviders());
    }
}