package org.freshmarker.api;

import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;

import java.util.Locale;

public interface Formatter {
  String format(TemplateObject object, Locale locale);
  default TemplateObject parse(String input, Locale locale) {
      return TemplateNull.NULL;
  }
}
