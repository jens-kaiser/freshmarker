package org.freshmarker.api;

import org.freshmarker.core.model.TemplateObject;

import java.util.Locale;

public interface Formatter {
  String format(TemplateObject object, Locale locale);
}
