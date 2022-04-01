package org.freshmarker.core.model;

import org.freshmarker.core.Environment;

public class TemplateMarkup implements TemplateObject {

  private final TemplateObject content;

  public TemplateMarkup(TemplateObject content) {
    if (content.isMarkup()) {
      this.content = ((TemplateMarkup) content).content;
    } else {
      this.content = content;
    }
  }

  @Override
  public boolean isMarkup() {
    return true;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    TemplateObject templateObject = content;
    do {
      templateObject = templateObject.evaluateToObject(environment);
    } while (templateObject != TemplateNull.NULL && !templateObject.isPrimitive() && !templateObject.isMarkup());
    String result = environment.getFormatter(templateObject.getClass()).format(templateObject, environment.getLocale());
    return environment.getOutputFormat().escape(result);
  }
}
