package org.freshmarker.core;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.providers.TemplateObjectProvider;

public class BaseEnvironment implements Environment {

  private final Map<String, Object> dataModel;
  private final Locale locale;
  private final List<TemplateObjectProvider> providers;
  private final OutputFormat outputFormat;

  public BaseEnvironment(Map<String, Object> dataModel, List<TemplateObjectProvider> providers, Locale locale,
      OutputFormat outputFormat) {
    this.dataModel = dataModel;
    this.locale = locale;
    this.outputFormat = outputFormat;
    this.providers = providers;
  }

  @Override
  public TemplateObject mapObject(Object object) {
    return wrap(object);
  }

  @Override
  public TemplateObject getValue(String name) {
    return wrap(dataModel.get(name));
  }

  private TemplateObject wrap(Object o) {
    if (o == null) {
      return TemplateNull.NULL;
    }
    if (o instanceof TemplateObject) {
      return (TemplateObject) o;
    }
    return providers.stream().map(p -> p.provide(this, o)).filter(Objects::nonNull)
        .findFirst().orElseThrow(() -> new UnsupportedDataTypeException("unsupported data type: " + o.getClass()));
  }

  @Override
  public Locale getLocale() {
    return locale;
  }

  public OutputFormat getOutputFormat() {
    return outputFormat;
  }
}
