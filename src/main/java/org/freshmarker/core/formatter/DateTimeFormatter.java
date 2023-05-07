package org.freshmarker.core.formatter;

import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;

public class DateTimeFormatter implements Formatter {

  private final LocaleLocal<java.time.format.DateTimeFormatter> formatter;

  public DateTimeFormatter(String pattern, ZoneId zoneId) {
    this.formatter = LocaleLocal.withInitial(l -> java.time.format.DateTimeFormatter.ofPattern(pattern, l).withZone(zoneId));
  }

  public DateTimeFormatter(String pattern) {
    this.formatter = LocaleLocal.withInitial(l -> java.time.format.DateTimeFormatter.ofPattern(pattern, l));
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    TemplatePrimitive<?> dateTime = (TemplatePrimitive<?>)object;
    return formatter.get(locale).format((TemporalAccessor) dateTime.getValue());
  }
}
