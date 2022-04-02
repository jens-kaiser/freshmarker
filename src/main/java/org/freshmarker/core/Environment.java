package org.freshmarker.core;

import java.util.Locale;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

public interface Environment {

  TemplateObject mapObject(Object object);

  TemplateObject getValue(String name);

  BuiltIn getBuildIn(Class<? extends TemplateObject> type, String name);

  <T extends TemplateObject> Formatter getFormatter(Class<T> type);

  Locale getLocale();

  OutputFormat getOutputFormat();
}
