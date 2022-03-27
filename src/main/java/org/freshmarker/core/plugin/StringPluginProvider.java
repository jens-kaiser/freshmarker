package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuildInComponent;
import org.freshmarker.core.buildin.BuildInFunction;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class StringPluginProvider implements PluginProvider {

  public void registerBuildIn(Map<String, BuildInComponent> buildIns) {
    register(buildIns, "boolean", this::toBoolean);
    register(buildIns, "upper_case", (x, y, e) -> process(x, String::toUpperCase));
    register(buildIns, "lower_case", (x, y, e) -> process(x, String::toLowerCase));
    register(buildIns, "trim", (x, y, e) -> process(x, String::trim));
    register(buildIns, "contains", this::contains, List.of(TemplateString.class));
    register(buildIns, "ends_with", this::endsWith, List.of(TemplateString.class));
  }

  protected void register(Map<String, BuildInComponent> buildIns, String name, BuildInFunction function,
      List<Class<? extends TemplateObject>> parameters) {
    buildIns.computeIfAbsent(name, k -> new BuildInComponent())
        .add(TemplateString.class, new TypedBuildIn(function, parameters));
  }

  private void register(Map<String, BuildInComponent> buildIns, String name, BuildInFunction function) {
    buildIns.computeIfAbsent(name, k -> new BuildInComponent()).add(TemplateString.class, new TypedBuildIn(function));
  }

  private TemplateObject contains(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    checkSingleStringParameter(parameter);
    String input = ((TemplateString) value).getValue();
    return input.contains(((TemplateString) parameter.get(0)).getValue()) ? TemplateBoolean.TRUE
        : TemplateBoolean.FALSE;
  }

  private TemplateObject endsWith(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    checkSingleStringParameter(parameter);
    String input = ((TemplateString) value).getValue();
    return input.endsWith(((TemplateString) parameter.get(0)).getValue()) ? TemplateBoolean.TRUE
        : TemplateBoolean.FALSE;
  }

  private void checkSingleStringParameter(List<TemplateObject> parameter) {
    if (parameter.size() != 1 || !(parameter.get(0) instanceof TemplateString)) {
      throw new IllegalArgumentException("invalid parameter");
    }
  }

  private TemplateObject toBoolean(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    String input = ((TemplateString) value).getValue();
    if ("true".equals(input)) {
      return TemplateBoolean.TRUE;
    }
    if ("false".equals(input)) {
      return TemplateBoolean.FALSE;
    }
    throw new IllegalArgumentException("cannot convert string to boolean: " + input);
  }

  private TemplateObject process(TemplateObject value, UnaryOperator<String> function) {
    String input = ((TemplateString) value).getValue();
    if (input == null) {
      return TemplateNull.NULL;
    }
    if (input.isEmpty()) {
      return value;
    }
    return new TemplateString(function.apply(input));
  }
}
