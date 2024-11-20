package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.providers.TemplateObjectProvider;

public interface PluginProvider {

  default void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {

  }

  default void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {

  }

  default void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {

  }

  default void registerTemplateObjectProvider(List<TemplateObjectProvider> providers) {

  }

  default void registerUserDirective(Map<String, UserDirective> directives) {

  }

  default void registerFunction(Map<String, TemplateFunction> functions) {

  }

  default void registerBuiltInVariableProviders(Map<String, Function<ProcessContext, TemplateObject>> providers) {

  }
}
