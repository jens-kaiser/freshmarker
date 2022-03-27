package org.freshmarker.core.model.date;

import java.util.Date;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.freshmarker.core.model.temporal.TemplateDateTime;

public class TemplateClassicDateTime extends TemplatePrimitive<Date> implements TemplateDateTime {

  public TemplateClassicDateTime(Date value) {
    super(value);
  }
}
