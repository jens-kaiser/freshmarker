package org.freshmarker.core;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ProcessContext {

  private Environment environment;
  private Writer writer;
  private final Map<Object, Map<Object, Object>> stores = new HashMap<>();

  public ProcessContext(Environment environment, Writer writer) {
    this.environment = environment;
    this.writer = writer;
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

  public void setWriter(Writer writer) {
    this.writer = writer;
  }

  public Map<Object, Object> getStore(Object key) {
    return stores.computeIfAbsent(key, k -> new HashMap<>());
  }
}
