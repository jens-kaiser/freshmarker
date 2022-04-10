package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.DelegatingOutputFormat;
import org.freshmarker.core.output.OutputFormat;

public class TemplateMarkup implements TemplateObject {

  private final TemplateObject content;
  private final OutputFormat outputFormat;

  public TemplateMarkup(TemplateObject content) {
    this(content, DelegatingOutputFormat.INSTANCE);
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
    TemplateObject templateObject = content;
    do {
      templateObject = templateObject.evaluateToObject(context);
    } while (templateObject != TemplateNull.NULL && !templateObject.isPrimitive() && !templateObject.isMarkup());
    if (templateObject.isMarkup()) {
      return templateObject.evaluate(context, TemplateString.class);
    }
    Environment environment = context.getEnvironment();
    String result = context.getFormatter(templateObject.getClass()).format(templateObject, environment.getLocale());
    return outputFormat.escape(environment, result);
  }
}
