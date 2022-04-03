package org.freshmarker.core.plugin;

import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateString;

public class BooleanPluginProvider implements PluginProvider {
  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
  }

  @BuiltInMethod("c")
  public static TemplateString computerBuildIn(TemplateBoolean value, Environment environment) {
    return new TemplateString(String.valueOf(value));
  }

  @BuiltInMethod("then")
  public static TemplateObject thenBuildIn(TemplateBoolean value, Environment environment, TemplateObject trueValue,
      TemplateObject falseValue) {
    if (value == TemplateBoolean.TRUE) {
      return trueValue.evaluateToObject(environment);
    }
    return falseValue.evaluateToObject(environment);
  }

  @BuiltInMethod("string")
  public static  TemplateString stringBuildIn(TemplateBoolean value, Environment environment, TemplateString trueValue,
      TemplateString falseValue) {
    return value == TemplateBoolean.TRUE ? trueValue : falseValue;
  }
}
