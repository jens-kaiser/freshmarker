package org.freshmarker.core.providers;

import org.freshmarker.core.model.TemplateObject;

public interface TemplateObjectProvider {

  TemplateObject provide(TemplateObjectMapper environment, Object object);
}
