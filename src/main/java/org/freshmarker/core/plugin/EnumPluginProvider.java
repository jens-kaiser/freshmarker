package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.primitive.TemplateEnum;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.providers.EnumTemplateObjectProvider;
import org.freshmarker.core.providers.TemplateObjectProvider;

public class EnumPluginProvider implements PluginProvider {
  @Override
  public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
    new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
  }

  @BuiltInMethod("c")
  public static TemplateString computerBuiltIn(TemplateEnum<?> value) {
    return new TemplateString(value.getValue().name());
  }

  @BuiltInMethod("ordinal")
  public static TemplateNumber ordinal(TemplateEnum<?> value) {
    return new TemplateNumber(value.getValue().ordinal());
  }

  @Override
  public void registerTemplateObjectProvider(List<TemplateObjectProvider> providers) {
    providers.add(new EnumTemplateObjectProvider());
  }
}
