package org.freshmarker.core;

import org.freshmarker.api.OutputFormat;
import org.freshmarker.api.TemplateLoader;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Locale;

public record LocalContext(Locale locale, OutputFormat outputFormat, ZoneId zoneId, Clock clock, TemplateLoader templateLoader) {
}
