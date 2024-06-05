package org.freshmarker.core;

import java.io.Writer;
import java.util.Locale;
import java.util.Optional;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

public interface Environment {

  TemplateObject mapObject(Object object);

  TemplateObject getValue(String name);

  Locale getLocale();

  OutputFormat getOutputFormat();

  UserDirective getDirective(String nameSpace, String name);

  TemplateFunction getFunction(String name);

  Writer getWriter();

  default Optional<Fragment> getNestedContent() {
    return Optional.empty();
  }

  default void setVariable(String name, TemplateObject value) {
    throw new UnsupportedOperationException();
  }

  <T extends TemplateObject> Formatter getFormatter(Class<T> type);
}
