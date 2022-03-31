package org.freshmarker.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;

public class BufferedEnvironment implements Environment {

  private final Environment wrapped;
  private final Map<String, TemplateObject> dataModel;

  public BufferedEnvironment(Environment wrapped, Map<String, TemplateObject> dataModel) {
    this.dataModel = dataModel;
    this.wrapped = wrapped;
  }

  public BufferedEnvironment(Environment wrapped) {
    this(wrapped, new HashMap<>());
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
  public TypedBuildIn getBuildIn(Class<? extends TemplateObject> type, String name) {
    return wrapped.getBuildIn(type, name);
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
