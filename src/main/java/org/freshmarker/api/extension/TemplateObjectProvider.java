package org.freshmarker.api.extension;

import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.providers.TemplateObjectMapper;

public interface TemplateObjectProvider {

  TemplateObject provide(TemplateObjectMapper environment, Object object);

}
