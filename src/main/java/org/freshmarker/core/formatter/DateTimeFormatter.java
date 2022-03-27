package org.freshmarker.core.formatter;

import java.util.Locale;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;

public class DateTimeFormatter implements Formatter {

  private final LocaleLocal<java.time.format.DateTimeFormatter> formatter;

  public DateTimeFormatter(String pattern) {
    this.formatter = LocaleLocal.withInitial(l -> java.time.format.DateTimeFormatter.ofPattern(pattern, l));
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    TemplateLocalDateTime dateTime = (TemplateLocalDateTime)object;
    return formatter.get(locale).format(dateTime.getValue());
  }
}
