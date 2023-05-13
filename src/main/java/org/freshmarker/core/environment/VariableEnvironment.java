package org.freshmarker.core.environment;

import java.util.HashMap;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateObject;

public class VariableEnvironment extends WrapperEnvironment {

  private final Map<String, TemplateObject> dataModel = new HashMap<>();

  public VariableEnvironment(Environment wrapped) {
    super(wrapped);
  }

  @Override
  public void setVariable(String name, TemplateObject value) {
    if (name.startsWith(".")) {
      throw new IllegalArgumentException("built-in variable name not allowed: " + name);
    }
    dataModel.put(name, value);
  }

  @Override
  public TemplateObject getValue(String name) {
    TemplateObject result = dataModel.get(name);
    return result != null ? result : wrapped.getValue(name);
  }
}
