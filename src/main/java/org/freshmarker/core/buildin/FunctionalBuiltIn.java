package org.freshmarker.core.buildin;

import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public class FunctionalBuiltIn implements BuiltIn {

  private final BuiltInFunction function;
  private final List<Class<? extends TemplateObject>> parameters;

  public FunctionalBuiltIn(BuiltInFunction function, List<Class<? extends TemplateObject>> parameters) {
    this.function = function;
    this.parameters = parameters;
  }

  public FunctionalBuiltIn(BuiltInFunction function) {
    this(function, List.of());
  }

  @Override
  public void validate(List<TemplateObject> parameters) {
    if (this.parameters.size() != parameters.size()) {
      throw new IllegalArgumentException("invalid parameter count: " + parameters.size());
    }
  }

  @Override
  public TemplateObject apply(TemplateObject value, List<TemplateObject> parameter, ProcessContext context) {
    return function.apply(value, parameter, context);
  }
}
