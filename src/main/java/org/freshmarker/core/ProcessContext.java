package org.freshmarker.core;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.StringFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.UndefinedOutputFormat;

public class ProcessContext {

  private static final StringFormatter STRING_FORMATTER = new StringFormatter();

  private Environment environment;
  private final Map<Object, Map<Object, Object>> stores = new HashMap<>();
  private final Map<BuiltInKey, BuiltIn> builtIns;
  private final Map<Class<? extends TemplateObject>, Formatter> formatter;
  private final Map<String, OutputFormat> outputs;

  public ProcessContext(Environment environment, Map<BuiltInKey, BuiltIn> builtIns,
      Map<Class<? extends TemplateObject>, Formatter> formatter, Map<String, OutputFormat> outputs) {
    this.environment = environment;
    this.builtIns = builtIns;
    this.formatter = formatter;
    this.outputs = outputs;
  }

  public ProcessContext(Environment environment, ProcessContext parent) {
    this(environment, parent.builtIns, parent.formatter, parent.outputs);
  }

  public Environment getEnvironment() {
    return environment;
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public Writer getWriter() {
    return environment.getWriter();
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

  public OutputFormat getOutputFormat(String name) {
    return outputs.getOrDefault(name, UndefinedOutputFormat.INSTANCE);
  }
}
