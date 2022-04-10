package org.freshmarker.core.model;

import java.util.Map;
import org.freshmarker.core.ProcessContext;

public class TemplateBean implements TemplateMap {

  private final Map<String, Object> map;

  public TemplateBean(Map<String, Object> map) {
    this.map = map;
  }

  public TemplateObject get(ProcessContext context, String name) {
    Object result = map.get(name);
    return result == null ? TemplateNull.NULL : context.getEnvironment().mapObject(result);
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return this;
  }
}
