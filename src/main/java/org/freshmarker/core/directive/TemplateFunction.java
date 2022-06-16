package org.freshmarker.core.directive;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public interface TemplateFunction {
  TemplateObject execute(ProcessContext context, List<TemplateObject> args);
}
