package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateObject;

public class TemplateDotKey implements TemplateExpression {

  private final TemplateObject map;
  private final String dotKey;

  public TemplateDotKey(TemplateObject map, String dotKey) {
    this.map = map;
    this.dotKey = dotKey;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    TemplateObject templateObject = map.evaluateToObject(environment);
    if (templateObject instanceof TemplateMap) {
      TemplateMap templateMap = (TemplateMap) templateObject;
      return templateMap.get(environment, dotKey);
    }
    throw new ProcessException("index out of range");
  }
}
