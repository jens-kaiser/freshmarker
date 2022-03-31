package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuildInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class BooleanPluginProvider implements PluginProvider {

  private static final BuildInKeyBuilder<TemplateBoolean> BUILDER = new BuildInKeyBuilder<>(TemplateBoolean.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, TypedBuildIn> buildIns) {
    buildIns.put(BUILDER.of("c"), new TypedBuildIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    buildIns.put(BUILDER.of("string"), new TypedBuildIn(this::string, List.of(TemplateString.class, TemplateString.class)));
    buildIns.put(BUILDER.of("then"), new TypedBuildIn(this::thenBuildIn, List.of(TemplateString.class, TemplateString.class)));
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
