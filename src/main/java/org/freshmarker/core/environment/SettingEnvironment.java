package org.freshmarker.core.environment;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.freshmarker.core.Environment;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

public class SettingEnvironment extends WrapperEnvironment {

  private final Locale locale;
  private final ZoneId zoneId;
  private final OutputFormat format;

  private final Map<Class<? extends TemplateObject>, Formatter> formatters;

  public SettingEnvironment(Environment wrapped, Locale locale, OutputFormat format, Map<Class<? extends TemplateObject>, Formatter> formatters, ZoneId zoneId) {
    super(wrapped);
    this.locale = locale;
    this.format = format;
    this.formatters = formatters;
    this.zoneId = zoneId;
  }

  @Override
  public Locale getLocale() {
    return locale != null ? locale : wrapped.getLocale();
  }

  @Override
  public ZoneId getZoneId() {
    return zoneId != null ? zoneId : wrapped.getZoneId();
  }

  @Override
  public OutputFormat getOutputFormat() {
    return format != null ? format : wrapped.getOutputFormat();
  }

    @Override
  public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
    Formatter formatter = formatters == null ? null : formatters.get(type);
    return formatter != null ? formatter : wrapped.getFormatter(type);
  }
}
