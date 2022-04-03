package org.freshmarker.core.plugin;

import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class NumberPluginProvider implements PluginProvider {

  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
  }

  @BuiltInMethod("c")
  public static TemplateString computerBuiltIn(TemplateNumber value, Environment environment) {
    return new TemplateString(String.valueOf(value));
  }

  @BuiltInMethod
  public static TemplateNumber abs(TemplateNumber value, Environment environment) {
    return value.abs();
  }

  @BuiltInMethod
  public static TemplateNumber sign(TemplateNumber value, Environment environment) {
    return value.sign();
  }
}
