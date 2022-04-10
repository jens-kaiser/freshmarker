package org.freshmarker.core;

import java.util.Locale;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

public interface Environment {

  TemplateObject mapObject(Object object);

  TemplateObject getValue(String name);

  Locale getLocale();

  OutputFormat getOutputFormat();
}
