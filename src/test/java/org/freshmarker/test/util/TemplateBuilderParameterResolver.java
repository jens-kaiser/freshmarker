package org.freshmarker.test.util;

import org.freshmarker.Configuration;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.DefaultTemplateBuilder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.time.ZoneId;
import java.util.Locale;

public class TemplateBuilderParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return DefaultTemplateBuilder.class == parameterContext.getParameter().getType() || TemplateBuilder.class == parameterContext.getParameter().getType();
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ExtensionContext.Store store = extensionContext.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        Configuration configuration = store.getOrComputeIfAbsent(Configuration.class, k -> new Configuration(), Configuration.class);
        return configuration.builder().withLocale(Locale.GERMANY).withZoneId(ZoneId.of("CET"));
    }
}
