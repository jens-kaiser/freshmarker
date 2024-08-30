package org.freshmarker.core.environment;

import java.util.HashMap;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateObject;

public class BufferedEnvironment extends WrapperEnvironment {

  private final Map<String, TemplateObject> dataModel;

  public BufferedEnvironment(Environment wrapped) {
    super(wrapped);
    this.dataModel = new HashMap<>();
  }

  @Override
  public TemplateObject getValue(String name) {
      return dataModel.computeIfAbsent(name, wrapped::getValue);
  }
}
