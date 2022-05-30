package org.freshmarker.core;

import java.io.Writer;
import java.util.Locale;
import org.freshmarker.core.directive.UserDirective;
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
  public Locale getLocale() {
    return wrapped.getLocale();
  }

  @Override
  public OutputFormat getOutputFormat() {
    return wrapped.getOutputFormat();
  }

  @Override
  public UserDirective getDirective(String name) {
    return wrapped.getDirective(name);
  }

  @Override
  public Writer getWriter() {
    return wrapped.getWriter();
  }

  @Override
  public TemplateObject getValue(String name) {
    return wrapped.getValue(name);
  }
}
