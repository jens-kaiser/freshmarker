package org.freshmarker.core.output;

import org.freshmarker.core.model.primitive.TemplateString;

public class NoEscapeFormat implements OutputFormat {

  public static final NoEscapeFormat INSTANCE = new NoEscapeFormat();

  @Override
  public TemplateString escape(String value) {
    return new TemplateString(value);
  }
}
