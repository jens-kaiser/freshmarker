package org.freshmarker.api;

import org.freshmarker.core.model.TemplateObject;

import java.util.function.Function;

/**
 * Implementations of this class creates template-model instances from java types.
 * The implementations are used in the {@link org.freshmarker.core.providers.MappingTemplateObjectProvider}
 * at the beginning of the {@link TemplateObjectProvider} chain.
 */
public interface TypeMapper extends Function<Object, TemplateObject> {
}
