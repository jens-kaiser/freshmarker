package org.freshmarker.core.formatter;

import java.util.Locale;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.temporal.TemplateLocalDate;

public class DateFormatter implements Formatter {

  private final LocaleLocal<java.time.format.DateTimeFormatter> formatter;

  public DateFormatter(String pattern) {
    this.formatter = LocaleLocal.withInitial(l -> java.time.format.DateTimeFormatter.ofPattern(pattern, l));
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    TemplateLocalDate date = (TemplateLocalDate)object;
    return formatter.get(locale).format(date.getValue());
  }
}
