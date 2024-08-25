package org.freshmarker.core.providers;

import java.util.List;
import java.util.Map;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateObject;

public class CompoundTemplateObjectProvider implements TemplateObjectProvider {

  @Override
  public TemplateObject provide(BaseEnvironment environment, Object o) {
    if (o instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> values = (List<Object>) o;
      return new TemplateListSequence(values);
    }
    if (o instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> values = (Map<String, Object>) o;
      return new TemplateBean(values, null);
    }
    return null;
  }
}
