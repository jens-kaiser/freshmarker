package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MacroEnvironment extends WrapperEnvironment {
  private final Map<String, TemplateObject> values;
  private final Fragment body;

  public MacroEnvironment(Environment environment, Map<String, TemplateObject> values, Fragment body) {
    super(environment);
    this.values = values;
    this.body = body;
  }

  @Override
  public TemplateObject getValue(String name) {
      return Objects.requireNonNullElseGet(values.get(name), () -> wrapped.getValue(name));
  }

  @Override
  public Optional<Fragment> getNestedContent() {
    return Optional.ofNullable(body);
  }
}
