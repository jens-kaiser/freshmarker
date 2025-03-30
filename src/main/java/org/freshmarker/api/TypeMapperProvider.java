package org.freshmarker.api;

import org.freshmarker.core.TypeMapper;

import java.util.Map;

/**
 * An {@link Extension} to add new type mapper.
 */
public interface TypeMapperProvider extends Extension {
    /**
     * Returns a map of type mapper
     * @return a map of type mapper
     */
    Map<Class<?>, TypeMapper> providerTypeMapper();
}
