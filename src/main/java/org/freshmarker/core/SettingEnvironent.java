package org.freshmarker.core;

import java.util.Locale;
import org.freshmarker.core.model.TemplateObject;

public class SettingEnvironent extends WrapperEnvironment {

  private final Locale locale;

  public SettingEnvironent(Environment wrapped, Locale locale) {
    super(wrapped);
    this.locale = locale;
  }

  @Override
  public Locale getLocale() {
    return locale != null ? locale : super.getLocale();
  }

  @Override
  public TemplateObject getValue(String name) {
    return wrapped.getValue(name);
  }
}
