package org.freshmarker.core.buildin;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public class FunctionalBuiltIn implements BuiltIn {

  private final BuiltInFunction function;

  public FunctionalBuiltIn(BuiltInFunction function) {
    this.function = function;
  }

  @Override
  public TemplateObject apply(TemplateObject value, List<TemplateObject> parameter, ProcessContext context) {
    return function.apply(value, parameter, context);
  }
}
