package org.freshmarker.core.formatter;

import java.text.NumberFormat;
import java.util.Locale;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class NumberFormatter implements Formatter {

  private final LocaleLocal<NumberFormat> numberFormat;

  public NumberFormatter() {
    this.numberFormat = LocaleLocal.withInitial(NumberFormat::getNumberInstance);
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    if (object instanceof TemplateNumber number) {
      return numberFormat.get(locale).format(number.getValue());
    }
    return "";
  }
}
