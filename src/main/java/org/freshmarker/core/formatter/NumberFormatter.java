package org.freshmarker.core.formatter;

import java.text.NumberFormat;
import java.util.Locale;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class NumberFormatter implements Formatter {

  private final LocaleLocal<NumberFormat> numberFormat;

  public NumberFormatter() {
    this.numberFormat = LocaleLocal.withInitial(NumberFormat::getNumberInstance);
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    return object.asNumber().map(TemplatePrimitive::getValue).map(v -> numberFormat.get(locale).format(v)).orElse("");
  }
}
