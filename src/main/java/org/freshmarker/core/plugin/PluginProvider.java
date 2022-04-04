package org.freshmarker.core.plugin;

import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;

public interface PluginProvider {

  default void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {

  }

  default void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {

  }

  default void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {

  }
}
