package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuildInComponent;
import org.freshmarker.core.buildin.BuildInFunction;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class BooleanPluginProvider implements PluginProvider {

  public void registerBuildIn(Map<String, BuildInComponent> buildIns) {
    register(buildIns, "c", (x, y, e) -> new TemplateString(String.valueOf(x)));
    register(buildIns, "string", this::string, List.of(TemplateString.class, TemplateString.class));
    register(buildIns, "then", this::thenBuildIn, List.of(TemplateString.class, TemplateString.class));
  }

  private void register(Map<String, BuildInComponent> buildIns, String name, BuildInFunction function) {
    buildIns.computeIfAbsent(name, k -> new BuildInComponent()).add(TemplateBoolean.class, new TypedBuildIn(function));
  }

  protected void register(Map<String, BuildInComponent> buildIns, String name, BuildInFunction function,
      List<Class<? extends TemplateObject>> parameters) {
    buildIns.computeIfAbsent(name, k -> new BuildInComponent())
        .add(TemplateBoolean.class, new TypedBuildIn(function, parameters));
  }

  private TemplateObject string(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    if (parameter.size() != 2) {
      throw new IllegalArgumentException("invalid number of parameters");
    }
    TemplateString trueValue = parameter.get(0).asString()
        .orElseThrow(() -> new IllegalArgumentException("invalid type of parameter"));
    TemplateString falseValue = parameter.get(1).asString()
        .orElseThrow(() -> new IllegalArgumentException("invalid type of parameter"));
    return value == TemplateBoolean.TRUE ? trueValue : falseValue;
  }

  private TemplateObject thenBuildIn(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    if (parameter.size() != 2) {
      throw new IllegalArgumentException("invalid number of parameters");
    }
    TemplateString trueValue = parameter.get(0).evaluateToObject(environment).asString()
        .orElseThrow(() -> new IllegalArgumentException("invalid type of parameter"));
    TemplateString falseValue = parameter.get(1).evaluateToObject(environment).asString()
        .orElseThrow(() -> new IllegalArgumentException("invalid type of parameter"));
    return value == TemplateBoolean.TRUE ? trueValue : falseValue;
  }
}
