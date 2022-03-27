package org.freshmarker.core.model.temporal;

public interface TemplateDate extends TemplateTemporal {

  @Override
  default DateTimeType getType() {
    return DateTimeType.DATE;
  }
}
