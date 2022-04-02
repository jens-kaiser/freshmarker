package org.freshmarker.core.plugin;

import java.util.Map;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuiltIn;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class NumberPluginProvider implements PluginProvider {

  private static final BuiltInKeyBuilder<TemplateNumber> BUILDER = new BuiltInKeyBuilder<>(TemplateNumber.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    builtIns.put(BUILDER.of("c"), new TypedBuiltIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    builtIns.put(BUILDER.of("abs"), new TypedBuiltIn((x, y, e) -> ((TemplateNumber)x).abs()));
    builtIns.put(BUILDER.of("sign"),new TypedBuiltIn((x, y, e) -> ((TemplateNumber)x).sign()));
  }
}
