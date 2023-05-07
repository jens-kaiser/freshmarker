package org.freshmarker.core.model.temporal;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.Instant;
import java.time.ZoneId;

public class TemplateInstant extends TemplatePrimitive<Instant> implements TemplateDateTime {
  public TemplateInstant(Instant value) {
    super(value);
  }

  @Override
  public TemplateZonedDateTime atZone(ZoneId zoneId) {
    return new TemplateZonedDateTime(getValue().atZone(zoneId));
  }

}
