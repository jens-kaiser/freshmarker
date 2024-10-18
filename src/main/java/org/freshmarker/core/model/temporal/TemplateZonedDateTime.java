package org.freshmarker.core.model.temporal;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.ZonedDateTime;

public class TemplateZonedDateTime extends TemplatePrimitive<ZonedDateTime> implements TemplateDateTime {
  public TemplateZonedDateTime(ZonedDateTime value) {
    super(value);
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
