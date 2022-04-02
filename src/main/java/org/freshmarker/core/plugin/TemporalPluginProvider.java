package org.freshmarker.core.plugin;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuiltIn;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.DurationFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateDuration;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplatePeriod;

public class TemporalPluginProvider implements PluginProvider {

  private static final BuiltInKeyBuilder<TemplateLocalDateTime> DATE_TIME_BUILDER = new BuiltInKeyBuilder<>(
      TemplateLocalDateTime.class);
  private static final BuiltInKeyBuilder<TemplateLocalDate> DATE_BUILDER = new BuiltInKeyBuilder<>(
      TemplateLocalDate.class);
  private static final BuiltInKeyBuilder<TemplateLocalTime> TIME_BUILDER = new BuiltInKeyBuilder<>(
      TemplateLocalTime.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    builtIns.put(DATE_TIME_BUILDER.of("date"),
        new TypedBuiltIn((x, y, e) -> new TemplateLocalDate(((TemplateLocalDateTime) x).getValue().toLocalDate())));
    builtIns.put(DATE_TIME_BUILDER.of("time"),
        new TypedBuiltIn((x, y, e) -> new TemplateLocalTime(((TemplateLocalDateTime) x).getValue().toLocalTime())));
    builtIns.put(DATE_TIME_BUILDER.of("c"), new TypedBuiltIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    builtIns.put(DATE_BUILDER.of("date"), new TypedBuiltIn((x, y, e) -> x));
    builtIns.put(DATE_BUILDER.of("c"), new TypedBuiltIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    builtIns.put(TIME_BUILDER.of("time"), new TypedBuiltIn((x, y, e) -> x));
    builtIns.put(TIME_BUILDER.of("c"), new TypedBuiltIn((x, y, e) -> new TemplateString(String.valueOf(x))));
  }

  @Override
  public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
    mapper.put(LocalDateTime.class, o -> new TemplateLocalDateTime((LocalDateTime) o));
    mapper.put(LocalDate.class, o -> new TemplateLocalDate((LocalDate) o));
    mapper.put(LocalTime.class, o -> new TemplateLocalTime((LocalTime) o));
    mapper.put(Duration.class, o -> new TemplateDuration((Duration) o));
    mapper.put(Period.class, o -> new TemplatePeriod((Period) o));
  }

  @Override
  public void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {
    formatter.put(TemplateLocalDateTime.class, new DateTimeFormatter("yyyy-MM-dd hh:mm:ss"));
    formatter.put(TemplateLocalDate.class, new DateFormatter("yyyy-MM-dd"));
    formatter.put(TemplateLocalTime.class, new TimeFormatter("hh:mm:ss"));
    formatter.put(TemplateDuration.class, new DurationFormatter());
    formatter.put(TemplatePeriod.class, new DurationFormatter());
  }
}
