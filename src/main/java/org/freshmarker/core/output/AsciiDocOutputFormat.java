package org.freshmarker.core.output;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateString;

public class AsciiDocOutputFormat implements OutputFormat {
  public static final AsciiDocOutputFormat INSTANCE = new AsciiDocOutputFormat();

  @Override
  public TemplateString comment(Environment environment, String value) {
    return new TemplateString("\n////\n" + value + "\n////\n");
  }
}
