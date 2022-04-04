package org.freshmarker.core.buildin;

import java.util.Objects;
import org.freshmarker.core.model.TemplateObject;

public class BuiltInKey {
  private final Class<? extends TemplateObject> type;
  private final String name;

  public BuiltInKey(Class<? extends TemplateObject> type, String name) {
    this.type = Objects.requireNonNull(type);
    this.name = Objects.requireNonNull(name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BuiltInKey that = (BuiltInKey) o;
    return type.equals(that.type) && name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name);
  }
}
