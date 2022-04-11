package org.freshmarker.core.buildin;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public interface BuiltIn extends BuiltInFunction {

  @Override
  TemplateObject apply(TemplateObject value, List<TemplateObject> parameter, ProcessContext context);
}
