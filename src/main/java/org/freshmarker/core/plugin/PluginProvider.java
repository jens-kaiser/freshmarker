package org.freshmarker.core.plugin;

import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.buildin.BuildInComponent;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.primitive.TemplateObject;

public interface PluginProvider {

  default void registerBuildIn(Map<String, BuildInComponent> buildIns) {

  }

  default void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {

  }

  default void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {

  }
}
