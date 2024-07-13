package org.freshmarker.core.model.temporal;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;

import java.time.ZoneId;

public interface TemplateDateTime extends TemplateTemporal {

  default TemplateZonedDateTime atZone(ZoneId zoneId) {
    throw new ProcessException("not supported");
  }

  default TemplateZonedDateTime at(ProcessContext context) {
    return atZone(context.getEnvironment().getZoneId());
  }
}
