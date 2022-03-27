package org.freshmarker.core.formatter;

import java.util.Locale;
import org.freshmarker.core.model.primitive.TemplateObject;

public class StringFormatter implements Formatter {

  @Override
  public String format(TemplateObject object, Locale locale) {
    return object.toString();
  }
}
