package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuildInFunction;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuildInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class LooperPluginProvider implements PluginProvider {
  private static final BuildInKeyBuilder<TemplateLooper> BUILDER = new BuildInKeyBuilder<>(TemplateLooper.class);

  private static final List<TemplateString> ITEM_PARITYTY = List.of(new TemplateString("odd"),
      new TemplateString("even"));

  private static final List<TemplateString> ITEM_PARITYTY_CAP = List.of(new TemplateString("Odd"),
      new TemplateString("Even"));

  @Override
  public void registerBuildIn(Map<BuildInKey, TypedBuildIn> buildIns) {
    register2(buildIns, "index", (x, y, e) -> ((TemplateLooper)x).getIndex());
    register2(buildIns, "counter", (x, y, e) -> ((TemplateLooper)x).getCounter());
    register2(buildIns, "is_first", (x, y, e) -> ((TemplateLooper)x).isFirst());
    register2(buildIns, "is_last", (x, y, e) -> ((TemplateLooper)x).isLast());
    register2(buildIns, "has_next", (x, y, e) -> ((TemplateLooper)x).hasNext());
    register2(buildIns, "item_parity", (x, y, e) -> ((TemplateLooper)x).cycle(ITEM_PARITYTY));
    register2(buildIns, "item_parity_cap", (x, y, e) -> ((TemplateLooper)x).cycle(ITEM_PARITYTY_CAP));
    register2(buildIns, "item_cycle",this::cycle);
  }

  private void register2(Map<BuildInKey, TypedBuildIn> buildIns, String name, BuildInFunction function) {
    buildIns.put(BUILDER.of(name), new TypedBuildIn(function));
  }

  private TemplateObject cycle(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    if (parameter.isEmpty()) {
      throw new IllegalArgumentException("invalid number of parameters");
    }
    TemplateLooper looper = (TemplateLooper)value;
    return parameter.get(looper.getIndex().getValue().intValue() % parameter.size());
  }
}
