package org.freshmarker.core.formatter;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Set;

import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.temporal.TemplateLocalTime;

public class TimeFormatter implements Formatter {

  private final LocaleLocal<java.time.format.DateTimeFormatter> formatter;

  public TimeFormatter(String pattern) {
    this.formatter = LocaleLocal.withInitial(l -> getDateTimeFormatter(pattern, l));
  }

  private static java.time.format.DateTimeFormatter getDateTimeFormatter(String pattern, Locale l) {
    if (Set.of("full", "long", "medium", "short").contains(pattern)) {
      FormatStyle style = FormatStyle.valueOf(pattern.toUpperCase());
      String localizedDateTimePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, style, IsoChronology.INSTANCE, l);
      return java.time.format.DateTimeFormatter.ofPattern(localizedDateTimePattern, l);
    }

    return java.time.format.DateTimeFormatter.ofPattern(pattern, l);
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    TemplateLocalTime dateTime = (TemplateLocalTime)object;
    return formatter.get(locale).format(dateTime.getValue());
  }
}
