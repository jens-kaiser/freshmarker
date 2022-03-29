package org.freshmarker.core;

import java.util.Locale;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.primitive.TemplateObject;

public interface Environment {

  TemplateObject mapObject(Object object);

  TemplateObject getValue(String name);

  TypedBuildIn getBuildIn(Class<? extends TemplateObject> type, String name);

  <T extends TemplateObject> Formatter getFormatter(Class<T> type);

  Locale getLocale();
}
