package org.freshmarker.core.environment;

import java.util.HashMap;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateObject;

public class BufferedEnvironment extends WrapperEnvironment {

  private final Map<String, TemplateObject> dataModel;

  public BufferedEnvironment(Environment wrapped, Map<String, TemplateObject> dataModel) {
    super(wrapped);
    this.dataModel = dataModel;
  }

  public BufferedEnvironment(Environment wrapped) {
    this(wrapped, new HashMap<>());
  }

  @Override
  public TemplateObject getValue(String name) {
    TemplateObject result = dataModel.get(name);
    if (result != null) {
      return result;
    }
    TemplateObject value = wrapped.getValue(name);
    dataModel.put(name, value);
    return value;
  }
}
