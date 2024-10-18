package org.freshmarker.core.model.temporal;

import java.time.LocalDateTime;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateLocalDateTime extends TemplatePrimitive<LocalDateTime> implements TemplateDateTime {
  public TemplateLocalDateTime(LocalDateTime value) {
    super(value);
  }
}
