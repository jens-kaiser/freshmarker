package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;

public class TemplateDotKey implements TemplateExpression {

  private final TemplateObject map;
  private final String dotKey;

  public TemplateDotKey(TemplateObject map, String dotKey) {
    this.map = map;
    this.dotKey = dotKey;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateObject templateObject = map.evaluateToObject(context);
    if (templateObject == TemplateNull.NULL) {
      return TemplateNull.NULL;
    }
    if (templateObject instanceof TemplateMap templateMap) {
      return templateMap.get(context, dotKey);
    }
    throw new ProcessException("index out of range: " + dotKey + " " + templateObject);
  }
}
