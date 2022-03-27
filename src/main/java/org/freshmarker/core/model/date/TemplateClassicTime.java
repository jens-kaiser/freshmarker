package org.freshmarker.core.model.date;

import java.sql.Time;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.freshmarker.core.model.temporal.TemplateTime;

public class TemplateClassicTime extends TemplatePrimitive<Time> implements TemplateTime {

  public TemplateClassicTime(Time value) {
    super(value);
  }
}
