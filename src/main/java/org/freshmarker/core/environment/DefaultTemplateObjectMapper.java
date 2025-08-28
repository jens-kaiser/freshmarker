package org.freshmarker.core.environment;

import org.freshmarker.core.UnsupportedDataTypeException;
import org.freshmarker.core.model.TemplateHash;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.providers.TemplateObjectMapper;
import org.freshmarker.core.providers.TemplateObjectProvider;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultTemplateObjectMapper implements TemplateObjectMapper {
    private final List<TemplateObjectProvider> providers;
    private final Map<Class<?>, TemplateObjectProvider> templateObjectProviderMap;

    public DefaultTemplateObjectMapper(List<TemplateObjectProvider> providers, Map<Class<?>, TemplateObjectProvider> templateObjectProviderMap) {
        this.providers = providers;
        this.templateObjectProviderMap = templateObjectProviderMap;
    }

    @Override
    public TemplateObject mapObject(Object object) {
        return wrap(object);
    }

    private TemplateObject wrap(Object o) {
        return switch (o) {
            case null -> TemplateNull.NULL;
            case Map.Entry entry -> new TemplateHash(entry);
            case TemplateObject templateObject -> templateObject;
            case Optional<?> optional -> optional.map(object -> wrapByProviders(o, object)).orElse(TemplateNull.NULL_OPTIONAL);
            case TemplateObjectSupplier<?> templateObjectSupplier -> wrapByProviders(o, templateObjectSupplier.get());
            default -> wrapByProviders(o, o);
        };
    }

    private TemplateObject wrapByProviders(Object o, Object current) {
        TemplateObjectProvider cached = templateObjectProviderMap.get(o.getClass());
        if (cached != null) {
            return cached.provide(this, current);
        }
        for (TemplateObjectProvider provider : providers) {
            TemplateObject object = provider.provide(this, current);
            if (object != null) {
                templateObjectProviderMap.put(current.getClass(), provider);
                return object;
            }
        }
        throw new UnsupportedDataTypeException("unsupported data type: " + o.getClass());
    }
}
