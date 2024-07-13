package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.DelegatingOutputFormat;
import org.freshmarker.core.output.OutputFormat;

public class TemplateMarkup implements TemplateObject {

  private static final DelegatingOutputFormat INSTANCE = new DelegatingOutputFormat();

  private final TemplateObject content;
  private final OutputFormat outputFormat;

  public TemplateMarkup(TemplateObject content) {
    this(content, INSTANCE);
  }

  public TemplateMarkup(TemplateObject content, OutputFormat outputFormat) {
    if (content.isMarkup()) {
      this.content = ((TemplateMarkup) content).content;
    } else {
      this.content = content;
    }
    this.outputFormat = outputFormat;
  }

  @Override
  public boolean isMarkup() {
    return true;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateObject templateObject = getTemplateObject(context);
    if (templateObject.isMarkup()) {
      return templateObject.evaluate(context, TemplateString.class);
    }
    Environment environment = context.getEnvironment();
    String result = environment.getFormatter(templateObject.getClass()).format(templateObject, environment.getLocale());
    return outputFormat.escape(environment, result);
  }

  public Class<?> getType() {
    return getClass();
  }

  private TemplateObject getTemplateObject(ProcessContext context) {
    TemplateObject templateObject = content;
    do {
      TemplateObject last = templateObject;
      templateObject = templateObject.evaluateToObject(context);
      if (last == templateObject && !templateObject.isPrimitive()) {
        throw new ProcessException("missing reduction detected. Unsupported primitive? " + templateObject.getModelType());
      }
    } while (templateObject != TemplateNull.NULL && !templateObject.isPrimitive() && !templateObject.isMarkup());
    if (templateObject == TemplateNull.NULL) {
      throw new ProcessException("null");
    }
    return templateObject;
  }
}
