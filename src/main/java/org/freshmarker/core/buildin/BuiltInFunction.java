package org.freshmarker.core.buildin;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public interface BuiltInFunction {
  TemplateObject apply(TemplateObject value, List<TemplateObject> parameter, ProcessContext context);
}
