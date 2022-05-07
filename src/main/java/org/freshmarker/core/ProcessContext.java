package org.freshmarker.core;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.StringFormatter;
import org.freshmarker.core.model.TemplateObject;

public class ProcessContext {
  private static final StringFormatter STRING_FORMATTER = new StringFormatter();

  private Environment environment;
  private final Writer writer;
  private final Map<Object, Map<Object, Object>> stores = new HashMap<>();
  private final Map<BuiltInKey, BuiltIn> builtIns;
  private final Map<Class<? extends TemplateObject>, Formatter> formatter;

  public ProcessContext(Environment environment, Writer writer, Map<BuiltInKey, BuiltIn> builtIns,
      Map<Class<? extends TemplateObject>, Formatter> formatter) {
    this.environment = environment;
    this.writer = writer;
    this.builtIns = builtIns;
    this.formatter = formatter;
  }

  public ProcessContext(Environment environment, ProcessContext parent) {
    this(environment, parent.getWriter(), parent.builtIns, parent.formatter);
  }

  public Environment getEnvironment() {
    return environment;
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public Writer getWriter() {
    return writer;
  }

  public Map<Object, Object> getStore(Object key) {
    return stores.computeIfAbsent(key, k -> new HashMap<>());
  }

  public BuiltIn getBuiltIn(Class<? extends TemplateObject> type, String name) {
    BuiltIn result = builtIns.get(new BuiltInKey(type, name));
    if (result == null) {
      throw new UnsupportedBuiltInException("unsupported builtin '" + name + "' for " + type.getSimpleName());
    }
    return result;
  }

  public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
    return formatter.getOrDefault(type, STRING_FORMATTER);
  }
}
