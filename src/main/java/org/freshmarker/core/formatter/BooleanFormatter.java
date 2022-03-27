package org.freshmarker.core.formatter;

import java.util.Locale;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateObject;

public class BooleanFormatter implements Formatter {

  private final String trueValue;
  private final String falseValue;

  public BooleanFormatter(String trueValue, String falseValue) {
    this.trueValue = trueValue;
    this.falseValue = falseValue;
  }

  @Override
  public String format(TemplateObject object, Locale locale) {
    return object == TemplateBoolean.TRUE ? trueValue : falseValue;
  }
}
