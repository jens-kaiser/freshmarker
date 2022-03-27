package org.freshmarker.core.formatter;

import java.util.Locale;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class DurationFormatter implements Formatter {

  @Override
  public String format(TemplateObject object, Locale locale) {
    return String.valueOf(((TemplatePrimitive<?>) object).getValue());
  }
}
