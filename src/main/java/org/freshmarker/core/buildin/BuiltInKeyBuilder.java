package org.freshmarker.core.buildin;

import org.freshmarker.core.model.TemplateObject;

public class BuiltInKeyBuilder<T extends TemplateObject> {

  private final Class<T> type;

  public BuiltInKeyBuilder(Class<T> type) {
    this.type = type;
  }

  public BuildInKey of(String name) {
    return new BuildInKey(type, name);
  }
}
