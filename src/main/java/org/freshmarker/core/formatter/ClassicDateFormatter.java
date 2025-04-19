package org.freshmarker.core.formatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.freshmarker.api.Formatter;
import org.freshmarker.core.model.date.TemplateClassicDate;
import org.freshmarker.core.model.TemplateObject;

public class ClassicDateFormatter implements Formatter {

  private final LocaleLocal<DateFormat> formatter;

  public ClassicDateFormatter(String pattern) {
    this.formatter = LocaleLocal.withInitial(l -> new SimpleDateFormat(pattern, l));
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    TemplateClassicDate date = (TemplateClassicDate)object;
    return formatter.get(locale).format(date.getValue());
  }
}
