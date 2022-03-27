package org.freshmarker.core.model.temporal;

import java.time.Duration;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateDuration extends TemplatePrimitive<Duration> {

  public TemplateDuration(Duration value) {
    super(value);
  }
}
