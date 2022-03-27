package org.freshmarker.core.model.temporal;

import java.time.Period;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplatePeriod extends TemplatePrimitive<Period> {

  public TemplatePeriod(Period value) {
    super(value);
  }
}
