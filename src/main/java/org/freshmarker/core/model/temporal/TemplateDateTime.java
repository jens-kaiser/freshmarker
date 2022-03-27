package org.freshmarker.core.model.temporal;

public interface TemplateDateTime extends TemplateTemporal {

  @Override
  default DateTimeType getType() {
    return DateTimeType.DATE_TIME;
  }
}
