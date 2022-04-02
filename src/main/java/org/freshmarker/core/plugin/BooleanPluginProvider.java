package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuiltIn;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateString;

public class BooleanPluginProvider implements PluginProvider {

  private static final BuiltInKeyBuilder<TemplateBoolean> BUILDER = new BuiltInKeyBuilder<>(TemplateBoolean.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    builtIns.put(BUILDER.of("c"), new TypedBuiltIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    builtIns.put(BUILDER.of("string"),
        new TypedBuiltIn(this::string, List.of(TemplateString.class, TemplateString.class)));
    builtIns.put(BUILDER.of("then"),
        new TypedBuiltIn(this::thenBuildIn, List.of(TemplateString.class, TemplateString.class)));
  }

  private TemplateObject string(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    if (parameter.size() != 2) {
      throw new IllegalArgumentException("invalid number of parameters");
    }
    TemplateString trueValue = parameter.get(0).asString()
        .orElseThrow(() -> new WrongTypeException("invalid type of parameter"));
    TemplateString falseValue = parameter.get(1).asString()
        .orElseThrow(() -> new WrongTypeException("invalid type of parameter"));
    return value == TemplateBoolean.TRUE ? trueValue : falseValue;
  }

  private TemplateObject thenBuildIn(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    if (parameter.size() != 2) {
      throw new IllegalArgumentException("invalid number of parameters");
    }
    if (value == TemplateBoolean.TRUE) {
      return parameter.get(0).evaluateToObject(environment);
    }
    return parameter.get(1).evaluateToObject(environment);
  }
}
