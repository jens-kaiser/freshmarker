package org.freshmarker.core.environment;

import java.util.Locale;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

public class SettingEnvironment extends WrapperEnvironment {

  private final Locale locale;
  private final OutputFormat format;

  public SettingEnvironment(Environment wrapped, Locale locale, OutputFormat format) {
    super(wrapped);
    this.locale = locale;
    this.format = format;
  }

  @Override
  public Locale getLocale() {
    return locale != null ? locale : wrapped.getLocale();
  }

  @Override
  public OutputFormat getOutputFormat() {
    return format != null ? format : wrapped.getOutputFormat();
  }

  @Override
  public TemplateObject getValue(String name) {
    return wrapped.getValue(name);
  }
}
