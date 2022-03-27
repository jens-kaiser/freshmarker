package org.freshmarker.core.model.temporal;

import java.time.LocalTime;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateLocalTime extends TemplatePrimitive<LocalTime> implements TemplateTime {

  public TemplateLocalTime(LocalTime value) {
    super(value);
  }
}
