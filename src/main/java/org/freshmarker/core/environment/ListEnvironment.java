package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateLoopVariable;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;

public class ListEnvironment extends WrapperEnvironment {
  private final String looperIdentifier;

  private final TemplateLooper looper;
  private final String identifier;

  private final TemplateLoopVariable loopVariable;


  public ListEnvironment(Environment wrapped, String identifier, String looperIdentifier, TemplateLooper looper,
      TemplateLoopVariable loopVariable) {
    super(wrapped);
    this.looper = looper;
    this.identifier = identifier;
    this.looperIdentifier = looperIdentifier;
    this.loopVariable = loopVariable;
  }

  @Override
  public TemplateObject getValue(String name) {
    if (looperIdentifier != null && looperIdentifier.equals(name)) {
      return looper;
    }
    return identifier.equals(name) ? loopVariable : wrapped.getValue(name);
  }
}
