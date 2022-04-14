package org.freshmarker.core.model.temporal;

public interface TemplateTime extends TemplateTemporal {

  @Override
  default DateTimeType getType() {
    return DateTimeType.TIME;
  }
}
