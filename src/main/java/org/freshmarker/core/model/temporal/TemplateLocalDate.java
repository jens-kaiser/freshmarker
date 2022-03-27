package org.freshmarker.core.model.temporal;

import java.time.LocalDate;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateLocalDate extends TemplatePrimitive<LocalDate> implements TemplateDate {

  public TemplateLocalDate(LocalDate value) {
    super(value);
  }
}
