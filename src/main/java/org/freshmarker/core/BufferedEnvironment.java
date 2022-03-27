package org.freshmarker.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.buildin.BuildInComponent;

public class BufferedEnvironment implements Environment {

  private final Environment wrapped;
  private final Map<String, TemplateObject> dataModel;

  public BufferedEnvironment(Environment wrapped) {
    this.dataModel = new HashMap<>();
    this.wrapped = wrapped;
  }

  @Override
  public TemplateObject mapObject(Object object) {
    return wrapped.mapObject(object);
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

  @Override
  public BuildInComponent getBuildIn(String name) {
    return wrapped.getBuildIn(name);
  }

  @Override
  public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
    return wrapped.getFormatter(type);
  }

  @Override
  public Locale getLocale() {
    return wrapped.getLocale();
  }
}
