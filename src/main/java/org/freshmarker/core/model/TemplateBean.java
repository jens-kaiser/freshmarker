package org.freshmarker.core.model;

import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateObject;

public class TemplateBean implements TemplateMap {

  private final Map<String, Object> map;

  public TemplateBean(Map<String, Object> map) {
    this.map = map;
  }

  public TemplateObject get(Environment environment, String name) {
    Object result = map.get(name);
    return result == null ? TemplateNull.NULL : environment.mapObject(result);
  }
}
