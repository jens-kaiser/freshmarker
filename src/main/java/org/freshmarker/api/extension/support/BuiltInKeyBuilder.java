package org.freshmarker.api.extension.support;

import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.model.TemplateObject;

/**
 * Builder for {@link BuiltInKey} instances. The builder is fixed for the type of Built-Ins.
 * @param <T>
 */
public class BuiltInKeyBuilder<T extends TemplateObject> {

  private final Class<T> type;

  public BuiltInKeyBuilder(Class<T> type) {
    this.type = type;
  }

  /**
   * generates a {@link BuiltInKey} instance with the specified name.
   * @param name the name of the Built-In
   * @return the {@link BuiltInKey} instance
   */
  public BuiltInKey of(String name) {
    return new BuiltInKey(type, name);
  }
}
