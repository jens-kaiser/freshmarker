package org.freshmarker.core.providers;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateObject;

public interface TemplateObjectProvider {

  TemplateObject provide(Environment environment, Object object);
}
