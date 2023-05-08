package org.freshmarker.core.model.temporal;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TemplateZonedDateTime extends TemplatePrimitive<ZonedDateTime> implements TemplateDateTime {
  public TemplateZonedDateTime(ZonedDateTime value) {
    super(value);
  }

  @Override
  public TemplateZonedDateTime atZone(ZoneId zoneId) {
    return new TemplateZonedDateTime(getValue().withZoneSameInstant(zoneId));
  }

  public TemplateLocalTime toLocalTime() {
    return new TemplateLocalTime(getValue().toLocalTime());
  }

  public TemplateLocalDate toLocalDate() {
    return new TemplateLocalDate(getValue().toLocalDate());
  }

  public TemplateLocalDateTime toLocalDateTime() {
    return new TemplateLocalDateTime(getValue().toLocalDateTime());
  }
}
