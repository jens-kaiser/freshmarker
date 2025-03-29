package org.freshmarker.api;

import org.freshmarker.core.TypeMapper;

import java.util.Map;

public interface TypeMapperProvider extends Extension {
    Map<Class<?>, TypeMapper> providerTypeMapper();
}
