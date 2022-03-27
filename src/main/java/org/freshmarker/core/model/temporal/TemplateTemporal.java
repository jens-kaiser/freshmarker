package org.freshmarker.core.model.temporal;

public interface TemplateTemporal {
  DateTimeType getType();

  default boolean isTime() {
    return DateTimeType.TIME == getType();
  }

  default boolean isDate() {
    return DateTimeType.DATE == getType();
  }

  default boolean isDateTime() {
    return DateTimeType.DATE_TIME == getType();
  }
}
