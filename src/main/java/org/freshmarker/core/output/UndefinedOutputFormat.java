package org.freshmarker.core.output;

import org.freshmarker.core.model.primitive.TemplateString;

public class UndefinedOutputFormat implements OutputFormat {

  public static final UndefinedOutputFormat INSTANCE = new UndefinedOutputFormat();

  @Override
  public TemplateString escape(String value) {
    return new TemplateString(value);
  }
}
