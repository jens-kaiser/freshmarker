package org.freshmarker.core.formatter;

import java.util.Locale;
import org.freshmarker.core.model.TemplateObject;

public interface Formatter {
  String format(TemplateObject object, Locale locale);
}
