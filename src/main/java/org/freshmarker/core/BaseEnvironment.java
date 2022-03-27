package org.freshmarker.core;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.buildin.BuildInComponent;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.StringFormatter;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.primitive.TemplateObject;

public class BaseEnvironment implements Environment {

  private static final StringFormatter FORMATTER = new StringFormatter();
  private final Map<String, BuildInComponent> buildIns;
  private final Map<String, Object> dataModel;
  private final Map<Class<?>, Function<Object, TemplateObject>> mapper;
  private final Map<Class<? extends TemplateObject>, Formatter> formatter;
  private final Locale locale;

  public BaseEnvironment(Map<String, BuildInComponent> buildIns,
      Map<String, Object> dataModel, Map<Class<?>, Function<Object, TemplateObject>> mapper,
      Map<Class<? extends TemplateObject>, Formatter> formatter, Locale locale) {
    this.buildIns = buildIns;
    this.dataModel = dataModel;
    this.mapper = mapper;
    this.formatter = formatter;
    this.locale = locale;
  }

  @Override
  public TemplateObject mapObject(Object object) {
    return wrap(object);
  }

  @Override
  public TemplateObject getValue(String name) {
    return wrap(dataModel.get(name));
  }

  private TemplateObject wrap(Object o) {
    if (o == null) {
      return TemplateNull.NULL;
    }
    if (o instanceof TemplateObject) {
      return (TemplateObject) o;
    }
    if (o instanceof List) {
      List<Object> values = (List<Object>) o;
      return new TemplateListSequence(values);
    }
    Function<Object, TemplateObject> mapping = mapper.get(o.getClass());
    if (mapping == null) {
      throw new IllegalArgumentException("unsupported data type: " + o.getClass());
    }
    return mapping.apply(o);
  }

  @Override
  public BuildInComponent getBuildIn(String name) {
    return buildIns.getOrDefault(name, new BuildInComponent());
  }

  @Override
  public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
    return formatter.getOrDefault(type, FORMATTER);
  }

  @Override
  public Locale getLocale() {
    return locale;
  }
}
