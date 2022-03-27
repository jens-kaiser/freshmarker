package org.freshmarker.core.model.temporal;

import org.freshmarker.core.model.temporal.DateTimeType;
import org.freshmarker.core.model.temporal.TemplateTemporal;

public interface TemplateTime extends TemplateTemporal {

  @Override
  default DateTimeType getType() {
    return DateTimeType.TIME;
  }
}
