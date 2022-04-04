package org.freshmarker.core.output;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateString;

public class NoEscapeFormat implements OutputFormat {

  public static final NoEscapeFormat INSTANCE = new NoEscapeFormat();

  @Override
  public TemplateString escape(Environment environment, String value) {
    return new TemplateString(value);
  }
}
