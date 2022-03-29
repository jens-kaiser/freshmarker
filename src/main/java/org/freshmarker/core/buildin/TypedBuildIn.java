package org.freshmarker.core.buildin;

import java.util.List;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateObject;

public class TypedBuildIn implements BuildInFunction {

  private final BuildInFunction function;
  private final List<Class<? extends TemplateObject>> parameters;

  public TypedBuildIn(BuildInFunction function, List<Class<? extends TemplateObject>> parameters) {
    this.function = function;
    this.parameters = parameters;
  }

  public TypedBuildIn(BuildInFunction function) {
    this(function, List.of());
  }

  public void validate(List<TemplateObject> parameters) {
    if (this.parameters.size() != parameters.size()) {
      throw new IllegalArgumentException("invalid parameter count: " + parameters.size());
    }
  }

  @Override
  public TemplateObject apply(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    return function.apply(value, parameter, environment);
  }
}
