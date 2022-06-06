package org.freshmarker.core.model;

import java.time.LocalDateTime;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;

public class TemplateBuiltInVariable implements TemplateExpression {

  private final String name;

  public TemplateBuiltInVariable(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    switch (name) {
      case "now":
        return new TemplateLocalDateTime(LocalDateTime.now());
      case "locale":
        return new TemplateString(context.getEnvironment().getLocale().toString());
      case "lang":
        return new TemplateString(context.getEnvironment().getLocale().getLanguage());
      default:
        throw new IllegalStateException("Unexpected value: " + name);
    }
  }
}
