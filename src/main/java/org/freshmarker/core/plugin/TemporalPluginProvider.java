package org.freshmarker.core.plugin;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuildInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.DurationFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateDuration;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplatePeriod;

public class TemporalPluginProvider implements PluginProvider {

  private static final BuildInKeyBuilder<TemplateLocalDateTime> DATE_TIME_BUILDER = new BuildInKeyBuilder<>(
      TemplateLocalDateTime.class);
  private static final BuildInKeyBuilder<TemplateLocalDate> DATE_BUILDER = new BuildInKeyBuilder<>(
      TemplateLocalDate.class);
  private static final BuildInKeyBuilder<TemplateLocalTime> TIME_BUILDER = new BuildInKeyBuilder<>(
      TemplateLocalTime.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, TypedBuildIn> buildIns) {
    buildIns.put(DATE_TIME_BUILDER.of("date"),
        new TypedBuildIn((x, y, e) -> new TemplateLocalDate(((TemplateLocalDateTime) x).getValue().toLocalDate())));
    buildIns.put(DATE_TIME_BUILDER.of("time"),
        new TypedBuildIn((x, y, e) -> new TemplateLocalTime(((TemplateLocalDateTime) x).getValue().toLocalTime())));
    buildIns.put(DATE_TIME_BUILDER.of("c"), new TypedBuildIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    buildIns.put(DATE_BUILDER.of("date"), new TypedBuildIn((x, y, e) -> x));
    buildIns.put(DATE_BUILDER.of("c"), new TypedBuildIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    buildIns.put(TIME_BUILDER.of("time"), new TypedBuildIn((x, y, e) -> x));
    buildIns.put(TIME_BUILDER.of("c"), new TypedBuildIn((x, y, e) -> new TemplateString(String.valueOf(x))));
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
