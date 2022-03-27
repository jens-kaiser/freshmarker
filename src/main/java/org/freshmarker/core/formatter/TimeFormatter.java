package org.freshmarker.core.formatter;

import java.util.Locale;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.model.temporal.TemplateLocalTime;

public class TimeFormatter implements Formatter {

  private final LocaleLocal<java.time.format.DateTimeFormatter> formatter;

  public TimeFormatter(String pattern) {
    this.formatter = LocaleLocal.withInitial(l -> java.time.format.DateTimeFormatter.ofPattern(pattern, l));
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    TemplateLocalTime dateTime = (TemplateLocalTime)object;
    return formatter.get(locale).format(dateTime.getValue());
  }
}
