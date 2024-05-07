package org.freshmarker.core;

import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.UndefinedOutputFormat;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ProcessContext {

  private Environment environment;
  private final Map<BuiltInKey, BuiltIn> builtIns;
  private final Map<String, OutputFormat> outputs;

  public ProcessContext(Environment environment, Map<BuiltInKey, BuiltIn> builtIns, Map<String, OutputFormat> outputs) {
    this.environment = environment;
    this.builtIns = builtIns;
    this.outputs = outputs;
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

  public BuiltIn getBuiltIn(Class<? extends TemplateObject> type, String name) {
    BuiltIn result = builtIns.get(new BuiltInKey(type, name));
    if (result == null) {
      throw new UnsupportedBuiltInException("unsupported builtin '" + name + "' for " + type.getSimpleName());
    }
    return result;
  }

  public OutputFormat getOutputFormat(String name) {
    return outputs.getOrDefault(name, UndefinedOutputFormat.INSTANCE);
  }
}
