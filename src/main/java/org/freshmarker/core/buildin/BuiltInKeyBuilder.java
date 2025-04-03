package org.freshmarker.core.buildin;

import org.freshmarker.api.extension.BuiltInKey;
import org.freshmarker.core.model.TemplateObject;

@Deprecated(since = "1.8.0", forRemoval = true)
public class BuiltInKeyBuilder<T extends TemplateObject> {

  private final Class<T> type;

  public BuiltInKeyBuilder(Class<T> type) {
    this.type = type;
  }

  /**
   * generates a {@link org.freshmarker.api.extension.BuiltInKey} instance with the specified name.
   * @param name the name of the Built-In
   * @return the {@link org.freshmarker.api.extension.BuiltInKey} instance
   */
  public org.freshmarker.api.extension.BuiltInKey of(String name) {
    return new BuiltInKey(type, name);
  }
}
