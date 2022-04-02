package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInFunction;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuiltIn;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class LooperPluginProvider implements PluginProvider {
  private static final BuiltInKeyBuilder<TemplateLooper> BUILDER = new BuiltInKeyBuilder<>(TemplateLooper.class);

  private static final List<TemplateString> ITEM_PARITYTY = List.of(new TemplateString("odd"),
      new TemplateString("even"));

  private static final List<TemplateString> ITEM_PARITYTY_CAP = List.of(new TemplateString("Odd"),
      new TemplateString("Even"));

  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    register2(builtIns, "index", (x, y, e) -> ((TemplateLooper)x).getIndex());
    register2(builtIns, "counter", (x, y, e) -> ((TemplateLooper)x).getCounter());
    register2(builtIns, "is_first", (x, y, e) -> ((TemplateLooper)x).isFirst());
    register2(builtIns, "is_last", (x, y, e) -> ((TemplateLooper)x).isLast());
    register2(builtIns, "has_next", (x, y, e) -> ((TemplateLooper)x).hasNext());
    register2(builtIns, "item_parity", (x, y, e) -> ((TemplateLooper)x).cycle(ITEM_PARITYTY));
    register2(builtIns, "item_parity_cap", (x, y, e) -> ((TemplateLooper)x).cycle(ITEM_PARITYTY_CAP));
    register2(builtIns, "item_cycle",this::cycle);
  }

  private void register2(Map<BuildInKey, BuiltIn> buildIns, String name, BuiltInFunction function) {
    buildIns.put(BUILDER.of(name), new TypedBuiltIn(function));
  }

  private TemplateObject cycle(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    if (parameter.isEmpty()) {
      throw new IllegalArgumentException("invalid number of parameters");
    }
    TemplateLooper looper = (TemplateLooper)value;
    return parameter.get(looper.getIndex().getValue().intValue() % parameter.size());
  }
}
