package org.freshmarker.core.buildin;

import org.freshmarker.core.model.primitive.TemplateObject;

public class BuildInKeyBuilder<T extends TemplateObject> {

  private final Class<T> type;

  public BuildInKeyBuilder(Class<T> type) {
    this.type = type;
  }

  public BuildInKey of(String name) {
    return new BuildInKey(type, name);
  }
}
