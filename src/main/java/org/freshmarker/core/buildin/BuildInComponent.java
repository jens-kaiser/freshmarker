package org.freshmarker.core.buildin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateObject;

public class BuildInComponent {

  private final Map<Class<? extends TemplateObject>, TypedBuildIn> implementations = new HashMap<>();

  public <T extends TemplateObject> void add(Class<T> type, TypedBuildIn buildIn) {
    implementations.put(type, buildIn);
  }

  public <T extends TemplateObject> TemplateObject handle(T value, List<TemplateObject> parameters,
      Environment environment) {
    return getBuildIn(value).handle(value, parameters, environment);
  }

  private <T extends TemplateObject> TypedBuildIn getBuildIn(T value) {
    TypedBuildIn typedBuildIn = implementations.get(value.getClass());
    if (typedBuildIn == null) {
      throw new ProcessException("buildin not found for type: " + value.getClass());
    }
    return typedBuildIn;
  }
}
