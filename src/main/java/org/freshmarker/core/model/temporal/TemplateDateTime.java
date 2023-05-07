package org.freshmarker.core.model.temporal;

import org.freshmarker.core.ProcessException;

import java.time.ZoneId;

public interface TemplateDateTime extends TemplateTemporal {

  @Override
  default DateTimeType getType() {
    return DateTimeType.DATE_TIME;
  }

  default TemplateZonedDateTime atZone(ZoneId zoneId) {
    throw new ProcessException("not supported");
  }
}
