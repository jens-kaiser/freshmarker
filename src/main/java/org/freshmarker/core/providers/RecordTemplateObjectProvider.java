package org.freshmarker.core.providers;

import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateRecordProvider;

public class RecordTemplateObjectProvider implements TemplateObjectProvider {

  private final TemplateRecordProvider recordProvider = new TemplateRecordProvider();

  @Override
  public TemplateObject provide(BaseEnvironment environment, Object o) {
    if (!o.getClass().isRecord()) {
      return null;
    }
    return new TemplateBean(recordProvider.provide(o, environment), o.getClass());
  }
}
