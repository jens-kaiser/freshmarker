package org.freshmarker.core.environment;

import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

public record Settings(Locale locale, ZoneId zoneId, OutputFormat format, Map<Class<? extends TemplateObject>, Formatter> formatters) {
}