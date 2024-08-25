package org.freshmarker.core.providers;

import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateObject;

public interface TemplateObjectProvider {

  TemplateObject provide(BaseEnvironment environment, Object object);
}
