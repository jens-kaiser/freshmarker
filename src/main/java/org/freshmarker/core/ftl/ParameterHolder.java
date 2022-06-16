package org.freshmarker.core.ftl;

import org.freshmarker.core.model.TemplateObject;

public class ParameterHolder {

  private final String name;
  private final TemplateObject defaultValue;

  public ParameterHolder(String name, TemplateObject defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  public String getName() {
    return name;
  }

  public TemplateObject getDefaultValue() {
    return defaultValue;
  }
}
