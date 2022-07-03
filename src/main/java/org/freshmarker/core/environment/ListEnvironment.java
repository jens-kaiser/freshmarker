package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;

public class ListEnvironment extends WrapperEnvironment {

  private final TemplateLooper looper;
  private final String identifier;

  public ListEnvironment(Environment wrapped, String identifier, TemplateLooper looper) {
    super(wrapped);
    this.looper = looper;
    this.identifier = identifier;
  }

  @Override
  public TemplateObject getValue(String name) {
    return identifier.equals(name) ? looper : wrapped.getValue(name);
  }
}
