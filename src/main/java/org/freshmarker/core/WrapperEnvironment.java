package org.freshmarker.core;

import java.util.Locale;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

public abstract class WrapperEnvironment implements Environment {

  protected final Environment wrapped;

  protected WrapperEnvironment(Environment wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public TemplateObject mapObject(Object object) {
    return wrapped.mapObject(object);
  }

  @Override
  public BuiltIn getBuiltIn(Class<? extends TemplateObject> type, String name) {
    return wrapped.getBuiltIn(type, name);
  }

  @Override
  public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
    return wrapped.getFormatter(type);
  }

  @Override
  public Locale getLocale() {
    return wrapped.getLocale();
  }

  @Override
  public OutputFormat getOutputFormat() {
    return wrapped.getOutputFormat();
  }
}
