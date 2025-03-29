package org.freshmarker.api;

import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;

public interface FormatterProvider extends Extension {
    Map<Class<? extends TemplateObject>, Formatter> providerFormatter();
}
