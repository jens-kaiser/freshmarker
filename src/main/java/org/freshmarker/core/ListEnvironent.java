package org.freshmarker.core;

import org.freshmarker.core.model.TemplateLoopValue;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;

public class ListEnvironent extends WrapperEnvironment {

  private final TemplateLooper looper;
  private final String identifier;
  private final String looperIdentifier;

  private final TemplateLoopValue loopValue;

  public ListEnvironent(Environment wrapped, String identifier, String looperIdentifier, TemplateLooper looper,
      TemplateLoopValue loopValue) {
    super(wrapped);
    this.looper = looper;
    this.identifier = identifier;
    this.looperIdentifier = looperIdentifier;
    this.loopValue = loopValue;
  }

  @Override
  public TemplateObject getValue(String name) {
    if (looperIdentifier != null && looperIdentifier.equals(name)) {
      return looper;
    }
    if (identifier.equals(name)) {
      return loopValue;
    }
    return wrapped.getValue(name);
  }
}
