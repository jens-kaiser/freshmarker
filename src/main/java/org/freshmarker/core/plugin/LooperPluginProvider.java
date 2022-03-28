package org.freshmarker.core.plugin;

import java.util.Map;
import org.freshmarker.core.buildin.BuildInComponent;
import org.freshmarker.core.buildin.BuildInFunction;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.model.TemplateLooper;

public class LooperPluginProvider implements PluginProvider {

  public void registerBuildIn(Map<String, BuildInComponent> buildIns) {
    register(buildIns, "index", (x, y, e) -> ((TemplateLooper)x).getIndex());
    register(buildIns, "is_first", (x, y, e) -> ((TemplateLooper)x).isFirst());
    register(buildIns, "is_last", (x, y, e) -> ((TemplateLooper)x).isLast());
  }

  private void register(Map<String, BuildInComponent> buildIns, String name, BuildInFunction function) {
    buildIns.computeIfAbsent(name, k -> new BuildInComponent()).add(TemplateLooper.class, new TypedBuildIn(function));
  }
}
