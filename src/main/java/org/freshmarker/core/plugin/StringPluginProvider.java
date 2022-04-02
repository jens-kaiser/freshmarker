package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInFunction;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuiltIn;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateString;

public class StringPluginProvider implements PluginProvider {

  private static final BuiltInKeyBuilder<TemplateString> BUILDER = new BuiltInKeyBuilder<>(TemplateString.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    register(builtIns, "boolean", this::toBoolean);
    register(builtIns, "upper_case", (x, y, e) -> process(x, s -> s.toUpperCase(e.getLocale())));
    register(builtIns, "lower_case", (x, y, e) -> process(x, s -> s.toLowerCase(e.getLocale())));
    register(builtIns, "trim", (x, y, e) -> process(x, String::trim));
    register(builtIns, "contains", this::contains, List.of(TemplateString.class));
    register(builtIns, "ends_with", this::endsWith, List.of(TemplateString.class));
  }

  protected void register(Map<BuildInKey, BuiltIn> buildIns, String name, BuiltInFunction function,
      List<Class<? extends TemplateObject>> parameters) {
    buildIns.put(BUILDER.of(name), new TypedBuiltIn(function, parameters));
  }

  private void register(Map<BuildInKey, BuiltIn> buildIns, String name, BuiltInFunction function) {
    buildIns.put(BUILDER.of(name), new TypedBuiltIn(function));
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
