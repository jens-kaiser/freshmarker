package org.freshmarker.core;

import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;

public class ListEnvironent extends WrapperEnvironment {

  private final TemplateLooper looper;
  private final String identifier;

  public ListEnvironent(Environment wrapped, String identifier, TemplateLooper looper) {
    super(wrapped);
    this.looper = looper;
    this.identifier = identifier;
  }

  @Override
  public TemplateObject getValue(String name) {
    return identifier.equals(name) ? looper : wrapped.getValue(name);
  }
}
