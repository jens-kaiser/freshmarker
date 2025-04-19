package org.freshmarker.core.formatter;

import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Set;

import org.freshmarker.api.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class DateTimeFormatter implements Formatter {

  private final LocaleLocal<java.time.format.DateTimeFormatter> formatter;

  public DateTimeFormatter(String pattern, ZoneId zoneId) {
    this.formatter = LocaleLocal.withInitial(l -> getDateTimeFormatter(pattern, l).withZone(zoneId));
  }

  public DateTimeFormatter(String pattern) {
    this.formatter = LocaleLocal.withInitial(l -> getDateTimeFormatter(pattern, l));
  }

  private static java.time.format.DateTimeFormatter getDateTimeFormatter(String pattern, Locale l) {
    if (Set.of("full", "long", "medium", "short").contains(pattern)) {
      FormatStyle style = FormatStyle.valueOf(pattern.toUpperCase());
      String localizedDateTimePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(style, style, IsoChronology.INSTANCE, l);
      return java.time.format.DateTimeFormatter.ofPattern(localizedDateTimePattern, l);
    }
    return java.time.format.DateTimeFormatter.ofPattern(pattern, l);
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    TemplatePrimitive<?> dateTime = (TemplatePrimitive<?>)object;
    return formatter.get(locale).format((TemporalAccessor) dateTime.getValue());
  }

  public java.time.format.DateTimeFormatter getFormatter(Locale locale) {
    return formatter.get(locale);
  }
}
