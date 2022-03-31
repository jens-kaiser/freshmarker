package org.freshmarker.core;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.StringFormatter;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateBeanProvider;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;

public class BaseEnvironment implements Environment {

  private static final StringFormatter STRING_FORMATTER = new StringFormatter();

  private final TemplateBeanProvider beanProvider = new TemplateBeanProvider();
  private final Map<BuildInKey, TypedBuildIn> buildIns;
  private final Map<String, Object> dataModel;
  private final Map<Class<?>, Function<Object, TemplateObject>> mapper;
  private final Map<Class<? extends TemplateObject>, Formatter> formatter;
  private final Locale locale;

  public BaseEnvironment(Map<BuildInKey, TypedBuildIn> buildIns,
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
    if (o instanceof Map) {
      Map<String, Object> values = (Map<String, Object>) o;
      return new TemplateBean(values);
    }
    Function<Object, TemplateObject> mapping = mapper.get(o.getClass());
    if (mapping != null) {
      return mapping.apply(o);
    }
    if (!o.getClass().isPrimitive() && !o.getClass().getName().startsWith("java")) {
      return new TemplateBean(beanProvider.provide(o, this));
    }
    throw new IllegalArgumentException("unsupported data type: " + o.getClass());
  }

  @Override
  public TypedBuildIn getBuildIn(Class<? extends TemplateObject> type, String name) {
    TypedBuildIn result = buildIns.get(new BuildInKey(type, name));
    if (result == null) {
      throw new IllegalArgumentException("unsupported plugin: " + name + " " + type);
    }
    return result;
  }

  @Override
  public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
    return formatter.getOrDefault(type, STRING_FORMATTER);
  }

  @Override
  public Locale getLocale() {
    return locale;
  }
}
