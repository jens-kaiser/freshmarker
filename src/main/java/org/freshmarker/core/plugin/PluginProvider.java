package org.freshmarker.core.plugin;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.features.FeatureSet;
import org.freshmarker.core.features.TemplateFeatures;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.providers.TemplateObjectProvider;

public interface PluginProvider {

  default void registerFeature(TemplateFeatures features) {

  }

  default void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {

  }

  default void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {

  }

  default void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter, FeatureSet featureSet) {
    registerFormatter(formatter);
  }

  default void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {

  }

  default void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns, FeatureSet featureSet) {
    registerBuildIn(builtIns);
  }

  default void registerTemplateObjectProvider(List<TemplateObjectProvider> providers) {

  }

  /**
   * @param directives the user directives to register
   * @deprecated use {@link org.freshmarker.api.NamedUserDirective} extension instead
   */
  @Deprecated(since = "2.0.0", forRemoval = true)
  default void registerUserDirective(Map<String, UserDirective> directives) {

  }

  /**
   * @param functions the functions to register
   * @deprecated use {@link org.freshmarker.api.NamedFunction} extension instead
   */
  @Deprecated(since = "2.0.0", forRemoval = true)
  default void registerFunction(Map<String, TemplateFunction> functions) {

  }

  default void registerBuiltInVariableProviders(Map<String, Function<ProcessContext, TemplateObject>> providers) {

  }
}
