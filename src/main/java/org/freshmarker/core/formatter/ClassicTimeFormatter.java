package org.freshmarker.core.formatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.freshmarker.api.Formatter;
import org.freshmarker.core.model.date.TemplateClassicTime;
import org.freshmarker.core.model.TemplateObject;

public class ClassicTimeFormatter implements Formatter {

  private final LocaleLocal<DateFormat> formatter;

  public ClassicTimeFormatter(String pattern) {
    this.formatter = LocaleLocal.withInitial(l -> new SimpleDateFormat(pattern, l));
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    TemplateClassicTime date = (TemplateClassicTime)object;
    return formatter.get(locale).format(date.getValue());
  }
}
