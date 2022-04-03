package org.freshmarker.core.plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class LooperPluginProvider implements PluginProvider {

  private static final List<TemplateObject> ITEM_PARITYTY = List.of(new TemplateString("odd"),
      new TemplateString("even"));

  private static final List<TemplateObject> ITEM_PARITYTY_CAP = List.of(new TemplateString("Odd"),
      new TemplateString("Even"));

  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
  }

  @BuiltInMethod
  public static TemplateNumber index(TemplateLooper value) {
    return value.getIndex();
  }

  @BuiltInMethod
  public static TemplateNumber counter(TemplateLooper value) {
    return value.getCounter();
  }

  @BuiltInMethod
  public static TemplateBoolean isFirst(TemplateLooper value) {
    return value.isFirst();
  }

  @BuiltInMethod
  public static TemplateBoolean isLast(TemplateLooper value) {
    return value.isLast();
  }

  @BuiltInMethod
  public static TemplateString itemParity(TemplateLooper value) {
    return (TemplateString) value.cycle(ITEM_PARITYTY);
  }

  @BuiltInMethod
  public static TemplateString itemParityCap(TemplateLooper value) {
    return (TemplateString) value.cycle(ITEM_PARITYTY_CAP);
  }

  @BuiltInMethod
  public static TemplateObject itemCycle(TemplateLooper value, Environment environment, TemplateObject... cycle) {
    return value.cycle(Arrays.asList(cycle));
  }

  @BuiltInMethod
  public static TemplateBoolean hasNext(TemplateLooper value) {
    return value.hasNext();
  }
}
