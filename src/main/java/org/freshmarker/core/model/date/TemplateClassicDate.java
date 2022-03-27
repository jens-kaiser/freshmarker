package org.freshmarker.core.model.date;

import java.sql.Date;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.freshmarker.core.model.temporal.TemplateDate;

public class TemplateClassicDate extends TemplatePrimitive<Date> implements TemplateDate {

  public TemplateClassicDate(Date value) {
    super(value);
  }
}
