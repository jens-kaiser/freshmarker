package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuildInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class NumberPluginProvider implements PluginProvider {

  private static final BuildInKeyBuilder<TemplateNumber> BUILDER = new BuildInKeyBuilder<>(TemplateNumber.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, TypedBuildIn> buildIns) {
    buildIns.put(BUILDER.of("c"), new TypedBuildIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    buildIns.put(BUILDER.of("abs"), new TypedBuildIn((x, y, e) -> ((TemplateNumber)x).abs()));
    buildIns.put(BUILDER.of("sign"),new TypedBuildIn((x, y, e) -> ((TemplateNumber)x).sign()));
  }
}
