package org.freshmarker.api;

import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.providers.TemplateObjectMapper;

/**
 * Implementations of this class creates template-model instances from java types.
 * For simple mappings see {@link TypeMapper}.
 */
public interface TemplateObjectProvider {

  /**
   * Returns a {@link TemplateObject} for the given {@link Object}
   * @param environment the templateObjectMapper to handle attribute values
   * @param object the current object
   * @return the template-model object
   */
  TemplateObject provide(TemplateObjectMapper environment, Object object);

}
