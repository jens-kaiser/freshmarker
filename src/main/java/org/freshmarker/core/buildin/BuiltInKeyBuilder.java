package org.freshmarker.core.buildin;

import org.freshmarker.core.model.TemplateObject;

@Deprecated(since = "1.8.0", forRemoval = true)
public class BuiltInKeyBuilder<T extends TemplateObject> extends org.freshmarker.api.extension.support.BuiltInKeyBuilder<T> {

  public BuiltInKeyBuilder(Class<T> type) {
    super(type);
  }
}
